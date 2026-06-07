package trinity.play2learn.backend.configs.seed.simulation.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.utils.InvestmentSimulationConstants;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationDateValidator;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateVariationService;

/**
 * Fase 8: evolución diaria de precios usando {@link IStockCalculateVariationService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockHistorySimulationService {

    private final IStockRepository stockRepository;
    private final IStockHistoryRepository stockHistoryRepository;
    private final IStockCalculateVariationService stockCalculateVariationService;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Stock> stocks = collector.getStocks();
        if (stocks.isEmpty()) {
            stocks = stockRepository.findAll();
        }
        if (stocks.isEmpty()) {
            log.warn("No hay stocks para simular historial de precios");
            return;
        }

        LocalDate endDate = timeline.getTo().toLocalDate();
        LocalDate today = LocalDate.now();
        if (endDate.isAfter(today)) {
            endDate = today;
        }

        LocalDateTime initialCreatedAt = timeline.getFrom();
        int totalRecords = 0;

        for (Stock stockRef : stocks) {
            Stock stock = stockRepository.findById(stockRef.getId()).orElse(stockRef);
            LocalDate startDay = initialCreatedAt.toLocalDate().plusDays(1);
            LocalDateTime previousHistoryAt = initialCreatedAt;
            int recordsForStock = 0;

            for (LocalDate day = startDay; !day.isAfter(endDate); day = day.plusDays(1)) {
                Double variation = stockCalculateVariationService.execute(stock);
                double newPrice = InvestmentSimulationConstants.clampStockPrice(
                    stock.getCurrentPrice() * (1 + variation / 100),
                    stock.getInitialPrice()
                );

                stock.setCurrentPrice(newPrice);
                stock = stockRepository.save(stock);

                LocalDateTime historyAt = day.atTime(LocalTime.of(1, 0));
                SimulationDateValidator.validateStockHistorySequence(previousHistoryAt, historyAt);

                StockHistory history = stockHistoryRepository.save(StockHistoryMapper.toModel(stock, variation));
                history.setCreatedAt(historyAt);
                stockHistoryRepository.save(history);

                previousHistoryAt = historyAt;
                recordsForStock++;
            }

            totalRecords += recordsForStock;
            log.debug("Stock {}: {} registros de historial diario", stock.getAbbreviation(), recordsForStock);
        }

        collector.addStockHistoryRecords(totalRecords);
        log.info("Fase 8: {} registros de StockHistory generados para {} acciones", totalRecords, stocks.size());
    }
}
