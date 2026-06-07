package trinity.play2learn.backend.configs.seed.simulation.services;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationDateValidator;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;

/**
 * Fase 7c: simula compra y venta de acciones sin recalcular precio en cada trade.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockTradeSimulationService {

    private final SimulationProperties properties;
    private final IStudentRepository studentRepository;
    private final IStockRepository stockRepository;
    private final IOrderRepository orderRepository;
    private final ITransactionGenerateService transactionGenerateService;
    private final IStockMoveService stockMoveService;
    private final IStockCalculateByWalletService stockCalculateByWalletService;
    private final IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        List<Stock> stocks = collector.getStocks();
        if (stocks.isEmpty()) {
            stocks = stockRepository.findAll();
        }
        if (stocks.isEmpty()) {
            log.warn("No hay stocks disponibles para simular trades");
            return;
        }

        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);
        Collections.shuffle(students, timeline.random());

        int target = Math.max(0, (int) Math.round(students.size() * properties.getStudentsTradingStocksRate()));
        LocalDateTime stockCreatedAt = timeline.getFrom();
        int buys = 0;
        int sells = 0;
        int openPositions = 0;

        for (int i = 0; i < target && i < students.size(); i++) {
            Student student = students.get(i);
            Wallet wallet = student.getWallet();
            int trades = timeline.randomIntBetween(1, properties.getMaxStockTradesPerStudent());
            boolean willSell = timeline.random().nextDouble() < properties.getStockSellAfterBuyRate();

            for (int t = 0; t < trades; t++) {
                Stock stock = stocks.get(timeline.random().nextInt(stocks.size()));
                int maxQty = computeMaxQuantity(wallet, stock);
                if (maxQty < properties.getMinStockBuyQuantity()) {
                    continue;
                }

                int quantity = timeline.randomIntBetween(
                    properties.getMinStockBuyQuantity(),
                    Math.min(maxQty, properties.getMaxStockBuyQuantity())
                );
                BigInteger qty = BigInteger.valueOf(quantity);
                double cost = stock.getCurrentPrice() * quantity;

                if (wallet.getBalance() < cost || stock.getAvailableAmount().compareTo(qty) < 0) {
                    continue;
                }

                LocalDateTime buyAt = timeline.randomBetween(timeline.getFrom(), timeline.getTo());
                SimulationDateValidator.validateOrderDate(buyAt, stockCreatedAt);

                Order buyOrder = saveOrder(OrderType.COMPRA, stock, wallet, qty, stock.getCurrentPrice(), buyAt);
                transactionGenerateService.generate(
                    TypeTransaction.STOCK,
                    cost,
                    "Compra simulada de acciones",
                    TransactionActor.ESTUDIANTE,
                    TransactionActor.SISTEMA,
                    wallet,
                    null, null, null, buyOrder, null, null
                );
                stockMoveService.toSold(stock, qty);
                stock = stockRepository.findById(stock.getId()).orElse(stock);
                walletUpdateInvestedBalanceService.execute(wallet);
                collector.incrementStockBuys();
                buys++;

                if (willSell && timeline.random().nextDouble() < properties.getStockSellAfterBuyRate()) {
                    BigInteger holdings = stockCalculateByWalletService.execute(stock, wallet);
                    if (holdings.compareTo(BigInteger.ZERO) > 0) {
                        BigInteger sellQty = holdings.min(qty);
                        LocalDateTime sellAt = timeline.nextAfter(buyAt, timeline.getTo());
                        SimulationDateValidator.validateOrderDate(sellAt, stockCreatedAt);

                        double sellAmount = stock.getCurrentPrice() * sellQty.doubleValue();
                        Order sellOrder = saveOrder(OrderType.VENTA, stock, wallet, sellQty, stock.getCurrentPrice(), sellAt);
                        transactionGenerateService.generate(
                            TypeTransaction.STOCK,
                            sellAmount,
                            "Venta simulada de acciones",
                            TransactionActor.SISTEMA,
                            TransactionActor.ESTUDIANTE,
                            wallet,
                            null, null, null, sellOrder, null, null
                        );
                        stockMoveService.toAvailable(stock, sellQty);
                        stock = stockRepository.findById(stock.getId()).orElse(stock);
                        walletUpdateInvestedBalanceService.execute(wallet);
                        collector.incrementStockSells();
                        sells++;
                    } else {
                        openPositions++;
                    }
                } else {
                    openPositions++;
                }
            }
        }

        log.info("Fase 7c: {} compras, {} ventas, ~{} posiciones abiertas", buys, sells, openPositions);
    }

    private int computeMaxQuantity(Wallet wallet, Stock stock) {
        if (stock.getCurrentPrice() <= 0) {
            return 0;
        }
        int byBalance = (int) Math.floor(wallet.getBalance() / stock.getCurrentPrice());
        int byAvailability = stock.getAvailableAmount().intValue();
        return Math.min(byBalance, byAvailability);
    }

    private Order saveOrder(
        OrderType type,
        Stock stock,
        Wallet wallet,
        BigInteger quantity,
        Double pricePerUnit,
        LocalDateTime createdAt
    ) {
        Order order = orderRepository.save(Order.builder()
            .orderType(type)
            .orderState(OrderState.EJECUTADA)
            .stock(stock)
            .wallet(wallet)
            .quantity(quantity)
            .pricePerUnit(pricePerUnit)
            .createdAt(createdAt)
            .build());
        return order;
    }
}
