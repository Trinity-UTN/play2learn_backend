package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;

@ExtendWith(MockitoExtension.class)
class StockFindAllServiceTest {

    @Mock
    private IStockRepository stockRepository;

    private StockFindAllService stockFindAllService;

    @BeforeEach
    void setUp() {
        stockFindAllService = new StockFindAllService(stockRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stocks exist When finding all Then returns all stocks")
        void whenStocksExist_returnsAllStocks() {
            // Given - Acciones existentes
            Stock stock1 = InvestmentTestMother.defaultStock();
            Stock stock2 = InvestmentTestMother.stock(1002L, "Otra Acción", "OA",
                    InvestmentTestMother.DEFAULT_INITIAL_PRICE, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT,
                    InvestmentTestMother.DEFAULT_SOLD_AMOUNT, RiskLevel.ALTO);
            List<Stock> expectedStocks = Arrays.asList(stock1, stock2);

            when(stockRepository.findAll()).thenReturn(expectedStocks);

            // When - Buscar todas las acciones
            Iterable<Stock> result = stockFindAllService.execute();

            // Then - Debe retornar todas las acciones
            verify(stockRepository).findAll();
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(expectedStocks);
        }

        @Test
        @DisplayName("Given no stocks exist When finding all Then returns empty list")
        void whenNoStocksExist_returnsEmptyList() {
            // Given - Sin acciones
            List<Stock> expectedStocks = Collections.emptyList();

            when(stockRepository.findAll()).thenReturn(expectedStocks);

            // When - Buscar todas las acciones
            Iterable<Stock> result = stockFindAllService.execute();

            // Then - Debe retornar lista vacía
            verify(stockRepository).findAll();
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }
}

