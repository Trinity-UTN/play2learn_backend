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
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;

@ExtendWith(MockitoExtension.class)
class StockHistoryFindByStockServiceTest {

    @Mock
    private IStockHistoryRepository stockHistoryRepository;

    private StockHistoryFindByStockService stockHistoryFindByStockService;

    @BeforeEach
    void setUp() {
        stockHistoryFindByStockService = new StockHistoryFindByStockService(stockHistoryRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stock with history When finding by stock Then returns list of history ordered by date ascending")
        void whenStockWithHistory_returnsListOrderedByDateAsc() {
            // Given - Acción con historial
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory history1 = InvestmentTestMother.defaultStockHistory();
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, 0.05,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0);
            List<StockHistory> expectedHistories = Arrays.asList(history1, history2);

            when(stockHistoryRepository.findByStockOrderByCreatedAtAsc(stock)).thenReturn(expectedHistories);

            // When - Buscar historial por acción
            List<StockHistory> result = stockHistoryFindByStockService.execute(stock);

            // Then - Debe retornar historial ordenado por fecha ascendente
            verify(stockHistoryRepository).findByStockOrderByCreatedAtAsc(stock);
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(expectedHistories);
        }

        @Test
        @DisplayName("Given stock with no history When finding by stock Then returns empty list")
        void whenNoHistory_returnsEmptyList() {
            // Given - Acción sin historial
            Stock stock = InvestmentTestMother.defaultStock();
            List<StockHistory> expectedHistories = Collections.emptyList();

            when(stockHistoryRepository.findByStockOrderByCreatedAtAsc(stock)).thenReturn(expectedHistories);

            // When - Buscar historial por acción
            List<StockHistory> result = stockHistoryFindByStockService.execute(stock);

            // Then - Debe retornar lista vacía
            verify(stockHistoryRepository).findByStockOrderByCreatedAtAsc(stock);
            assertThat(result).isEmpty();
        }
    }
}

