package trinity.play2learn.backend.investment.stock.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindAllService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateSpecificService;

@ExtendWith(MockitoExtension.class)
class StockUpdateServiceTest {

    @Mock
    private IStockFindAllService stockFindAllService;

    @Mock
    private IStockUpdateSpecificService stockUpdateSpecificService;

    private StockUpdateService stockUpdateService;

    @BeforeEach
    void setUp() {
        stockUpdateService = new StockUpdateService(
            stockFindAllService,
            stockUpdateSpecificService
        );
    }

    @Nested
    @DisplayName("cu78updateStock")
    class Cu78updateStock {

        @Test
        @DisplayName("Given multiple stocks When updating Then updates all stocks")
        void whenMultipleStocks_updatesAllStocks() {
            // Given - Múltiples acciones
            Stock stock1 = InvestmentTestMother.defaultStock();
            Stock stock2 = InvestmentTestMother.stock(1002L, "Otra Acción", "OA",
                    InvestmentTestMother.DEFAULT_INITIAL_PRICE, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT,
                    InvestmentTestMother.DEFAULT_SOLD_AMOUNT, trinity.play2learn.backend.investment.stock.models.RiskLevel.ALTO);
            List<Stock> stocks = Arrays.asList(stock1, stock2);

            when(stockFindAllService.execute()).thenReturn(stocks);

            // When - Ejecutar actualización
            stockUpdateService.cu78updateStock();

            // Then - Debe actualizar todas las acciones
            verify(stockFindAllService).execute();
            verify(stockUpdateSpecificService).execute(stock1);
            verify(stockUpdateSpecificService).execute(stock2);
        }

        @Test
        @DisplayName("Given empty list of stocks When updating Then does nothing")
        void whenEmptyList_doesNothing() {
            // Given - Lista vacía
            when(stockFindAllService.execute()).thenReturn(List.of());

            // When - Ejecutar actualización
            stockUpdateService.cu78updateStock();

            // Then - No debe actualizar nada
            verify(stockFindAllService).execute();
            verify(stockUpdateSpecificService, never()).execute(any(Stock.class));
        }
    }
}

