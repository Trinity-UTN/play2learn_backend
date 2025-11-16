package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;

@ExtendWith(MockitoExtension.class)
class StockHistoryFindByStockAndRangeServiceTest {

    @Mock
    private IStockHistoryRepository stockHistoryRepository;

    private StockHistoryFindByStockAndRangeService stockHistoryFindByStockAndRangeService;

    @BeforeEach
    void setUp() {
        stockHistoryFindByStockAndRangeService = new StockHistoryFindByStockAndRangeService(
            stockHistoryRepository
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stock and date range When finding by stock and range Then returns list of history in range")
        void whenStockAndRange_returnsListInRange() {
            // Given - Acción y rango de fechas
            Stock stock = InvestmentTestMother.defaultStock();
            LocalDateTime startRange = LocalDateTime.now().minusDays(7);
            LocalDateTime endRange = LocalDateTime.now();
            StockHistory history1 = InvestmentTestMother.defaultStockHistory();
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, 0.05,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0);
            List<StockHistory> expectedHistories = Arrays.asList(history1, history2);

            when(stockHistoryRepository.findByStockAndCreatedAtBetweenOrderByCreatedAtAsc(stock, startRange, endRange))
                    .thenReturn(expectedHistories);

            // When - Buscar historial por acción y rango
            List<StockHistory> result = stockHistoryFindByStockAndRangeService.execute(stock, startRange, endRange);

            // Then - Debe retornar historial en el rango
            verify(stockHistoryRepository).findByStockAndCreatedAtBetweenOrderByCreatedAtAsc(stock, startRange,
                    endRange);
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(expectedHistories);
        }

        @Test
        @DisplayName("Given stock and date range with no history When finding by stock and range Then returns empty list")
        void whenNoHistoryInRange_returnsEmptyList() {
            // Given - Sin historial en el rango
            Stock stock = InvestmentTestMother.defaultStock();
            LocalDateTime startRange = LocalDateTime.now().minusDays(7);
            LocalDateTime endRange = LocalDateTime.now();
            List<StockHistory> expectedHistories = Collections.emptyList();

            when(stockHistoryRepository.findByStockAndCreatedAtBetweenOrderByCreatedAtAsc(stock, startRange, endRange))
                    .thenReturn(expectedHistories);

            // When - Buscar historial por acción y rango
            List<StockHistory> result = stockHistoryFindByStockAndRangeService.execute(stock, startRange, endRange);

            // Then - Debe retornar lista vacía
            verify(stockHistoryRepository).findByStockAndCreatedAtBetweenOrderByCreatedAtAsc(stock, startRange,
                    endRange);
            assertThat(result).isEmpty();
        }
    }
}

