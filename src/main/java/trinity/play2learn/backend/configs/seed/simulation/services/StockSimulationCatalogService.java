package trinity.play2learn.backend.configs.seed.simulation.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.templates.StockSimulationTemplateFactory;
import trinity.play2learn.backend.configs.seed.simulation.utils.InvestmentSimulationConstants;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;

/**
 * Fase 6: crea catálogo de acciones simuladas con precios accesibles.
 *
 * <p>Idempotente: si ya existen stocks en BD, omite la creación.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockSimulationCatalogService {

    private final SimulationProperties properties;
    private final IStudentRepository studentRepository;
    private final IStockRepository stockRepository;
    private final IStockHistoryRepository stockHistoryRepository;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Stock> existing = stockRepository.findAll();
        if (!existing.isEmpty()) {
            log.warn("Catálogo de stocks ya existe ({} acciones) — omitiendo fase 6", existing.size());
            existing.forEach(collector::addStock);
            return;
        }

        double referencePrice = calculateReferenceInitialPrice();
        int count = timeline.randomIntBetween(
            properties.getMinStocksToCreate(),
            properties.getMaxStocksToCreate()
        );

        List<StockRegisterRequestDto> catalog = StockSimulationTemplateFactory.buildCatalog(
            count, referencePrice, timeline.random()
        );

        LocalDateTime stockCreatedAt = timeline.getFrom();
        int bajo = 0;
        int medio = 0;
        int alto = 0;

        for (StockRegisterRequestDto dto : catalog) {
            Stock stock = stockRepository.save(StockMapper.toModel(dto));
            collector.addStock(stock);
            collector.incrementStocksCreated();

            StockHistory history = stockHistoryRepository.save(StockHistoryMapper.toModel(stock, 0.0));
            history.setCreatedAt(stockCreatedAt);
            stockHistoryRepository.save(history);
            collector.addStockHistoryRecords(1);

            switch (stock.getRiskLevel()) {
                case BAJO -> bajo++;
                case MEDIO -> medio++;
                case ALTO -> alto++;
            }

            log.debug("Stock simulado creado: {} ({}) precio={} riesgo={}",
                stock.getName(), stock.getAbbreviation(), stock.getInitialPrice(), stock.getRiskLevel());
        }

        log.info(
            "Fase 6: {} stocks creadas — BAJO={}, MEDIO={}, ALTO={}, precio ref={}",
            count, bajo, medio, alto, referencePrice
        );
    }

    private double calculateReferenceInitialPrice() {
        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);
        if (students.isEmpty()) {
            return properties.getStockInitialPriceMin();
        }

        List<Double> balances = students.stream()
            .map(s -> s.getWallet().getBalance())
            .sorted()
            .toList();

        double p25 = balances.get(Math.max(0, (int) Math.floor(balances.size() * 0.25)));
        double computed = p25 * properties.getStockInitialPriceMaxBalanceFactor();
        double maxAllowed = InvestmentSimulationConstants.STOCK_INITIAL_PRICE_ABSOLUTE_MAX;
        return Math.max(
            properties.getStockInitialPriceMin(),
            Math.min(computed, maxAllowed)
        );
    }
}
