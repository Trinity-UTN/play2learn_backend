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
import trinity.play2learn.backend.investment.stock.models.RiskLevel;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;

@ExtendWith(MockitoExtension.class)
class StockCalculateAmountInvestedServiceTest {

    @Mock
    private IStockRepository stockRepository;

    @Mock
    private IStockCalculateByWalletService stockCalculateByWalletService;

    private StockCalculateAmountInvestedService stockCalculateAmountInvestedService;

    @BeforeEach
    void setUp() {
        stockCalculateAmountInvestedService = new StockCalculateAmountInvestedService(
            stockRepository,
            stockCalculateByWalletService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with stocks and quantities When calculating amount invested Then returns sum of stock prices times quantities")
        void whenWalletWithStocks_returnsSumOfPricesTimesQuantities() {
            // Given - Wallet con acciones y cantidades
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Stock stock1 = InvestmentTestMother.defaultStock();
            Stock stock2 = InvestmentTestMother.stock(1002L, "Otra Acción", "OA",
                    InvestmentTestMother.DEFAULT_INITIAL_PRICE, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT,
                    InvestmentTestMother.DEFAULT_SOLD_AMOUNT, RiskLevel.ALTO);
            List<Stock> stocks = Arrays.asList(stock1, stock2);
            BigInteger quantity1 = InvestmentTestMother.DEFAULT_QUANTITY;
            BigInteger quantity2 = BigInteger.valueOf(5);
            Double expectedTotal = (stock1.getCurrentPrice() * quantity1.doubleValue())
                    + (stock2.getCurrentPrice() * quantity2.doubleValue());

            when(stockRepository.findAll()).thenReturn(stocks);
            when(stockCalculateByWalletService.execute(stock1, wallet)).thenReturn(quantity1);
            when(stockCalculateByWalletService.execute(stock2, wallet)).thenReturn(quantity2);

            // When - Calcular monto invertido
            Double result = stockCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar suma de precios por cantidades
            verify(stockRepository).findAll();
            verify(stockCalculateByWalletService).execute(stock1, wallet);
            verify(stockCalculateByWalletService).execute(stock2, wallet);
            assertThat(result).isEqualTo(expectedTotal);
        }

        @Test
        @DisplayName("Given wallet with no stocks When calculating amount invested Then returns zero")
        void whenNoStocks_returnsZero() {
            // Given - Sin acciones
            Wallet wallet = InvestmentTestMother.defaultWallet();
            List<Stock> stocks = Collections.emptyList();

            when(stockRepository.findAll()).thenReturn(stocks);

            // When - Calcular monto invertido
            Double result = stockCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar cero
            verify(stockRepository).findAll();
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("Given wallet with stocks but zero quantities When calculating amount invested Then returns zero")
        void whenStocksWithZeroQuantities_returnsZero() {
            // Given - Acciones con cantidades cero
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Stock stock = InvestmentTestMother.defaultStock();
            List<Stock> stocks = Arrays.asList(stock);
            BigInteger zeroQuantity = BigInteger.ZERO;

            when(stockRepository.findAll()).thenReturn(stocks);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(zeroQuantity);

            // When - Calcular monto invertido
            Double result = stockCalculateAmountInvestedService.execute(wallet);

            // Then - Debe retornar cero
            assertThat(result).isZero();
        }
    }
}

