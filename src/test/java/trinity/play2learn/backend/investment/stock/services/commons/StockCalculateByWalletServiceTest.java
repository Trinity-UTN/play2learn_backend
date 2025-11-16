package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;

@ExtendWith(MockitoExtension.class)
class StockCalculateByWalletServiceTest {

    @Mock
    private IOrderRepository orderRepository;

    private StockCalculateByWalletService stockCalculateByWalletService;

    @BeforeEach
    void setUp() {
        stockCalculateByWalletService = new StockCalculateByWalletService(orderRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with executed buy orders When calculating quantity Then returns sum of bought quantities")
        void whenBuyOrders_returnsSumOfBoughtQuantities() {
            // Given - Wallet con órdenes de compra ejecutadas
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Stock stock = InvestmentTestMother.defaultStock();
            Order buyOrder1 = InvestmentTestMother.orderExecuted(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderType.COMPRA, InvestmentTestMother.DEFAULT_QUANTITY);
            Order buyOrder2 = InvestmentTestMother.orderExecuted(1102L, OrderType.COMPRA,
                    BigInteger.valueOf(5));
            List<Order> orders = Arrays.asList(buyOrder1, buyOrder2);
            BigInteger expectedTotal = buyOrder1.getQuantity().add(buyOrder2.getQuantity());

            when(orderRepository.findByWalletAndStockAndOrderState(wallet, stock, OrderState.EJECUTADA))
                    .thenReturn(orders);

            // When - Calcular cantidad en wallet
            BigInteger result = stockCalculateByWalletService.execute(stock, wallet);

            // Then - Debe retornar suma de cantidades compradas
            verify(orderRepository).findByWalletAndStockAndOrderState(wallet, stock, OrderState.EJECUTADA);
            assertThat(result).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Given wallet with executed sell orders When calculating quantity Then returns difference")
        void whenSellOrders_returnsDifference() {
            // Given - Wallet con órdenes de venta ejecutadas
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Stock stock = InvestmentTestMother.defaultStock();
            Order buyOrder = InvestmentTestMother.orderExecuted(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderType.COMPRA, InvestmentTestMother.DEFAULT_QUANTITY);
            Order sellOrder = InvestmentTestMother.orderExecuted(1102L, OrderType.VENTA,
                    BigInteger.valueOf(3));
            List<Order> orders = Arrays.asList(buyOrder, sellOrder);
            BigInteger expectedTotal = buyOrder.getQuantity().subtract(sellOrder.getQuantity());

            when(orderRepository.findByWalletAndStockAndOrderState(wallet, stock, OrderState.EJECUTADA))
                    .thenReturn(orders);

            // When - Calcular cantidad en wallet
            BigInteger result = stockCalculateByWalletService.execute(stock, wallet);

            // Then - Debe retornar diferencia (compra - venta)
            verify(orderRepository).findByWalletAndStockAndOrderState(wallet, stock, OrderState.EJECUTADA);
            assertThat(result).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Given wallet with no executed orders When calculating quantity Then returns zero")
        void whenNoExecutedOrders_returnsZero() {
            // Given - Wallet sin órdenes ejecutadas
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Stock stock = InvestmentTestMother.defaultStock();
            List<Order> orders = Collections.emptyList();

            when(orderRepository.findByWalletAndStockAndOrderState(wallet, stock, OrderState.EJECUTADA))
                    .thenReturn(orders);

            // When - Calcular cantidad en wallet
            BigInteger result = stockCalculateByWalletService.execute(stock, wallet);

            // Then - Debe retornar cero
            verify(orderRepository).findByWalletAndStockAndOrderState(wallet, stock, OrderState.EJECUTADA);
            assertThat(result).isZero();
        }
    }
}

