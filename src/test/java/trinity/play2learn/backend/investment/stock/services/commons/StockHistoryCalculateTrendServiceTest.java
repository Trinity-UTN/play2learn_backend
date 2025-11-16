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
class StockHistoryCalculateTrendServiceTest {

    @Mock
    private IStockHistoryRepository stockHistoryRepository;

    private StockHistoryCalculateTrendService stockHistoryCalculateTrendService;

    @BeforeEach
    void setUp() {
        stockHistoryCalculateTrendService = new StockHistoryCalculateTrendService(stockHistoryRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stock with positive variations sum When calculating trend Then returns true (bullish)")
        void whenPositiveVariationsSum_returnsTrue() {
            // Given - Acción con suma de variaciones positiva
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory history1 = InvestmentTestMother.defaultStockHistory();
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, 5.0,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0);
            StockHistory history3 = InvestmentTestMother.stockHistoryWithVariation(1203L, 3.0,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 8.0);
            List<StockHistory> histories = Arrays.asList(history1, history2, history3);
            // Suma de variaciones: 0.0 + 5.0 + 3.0 = 8.0 > 0

            when(stockHistoryRepository.findTop10ByStockOrderByCreatedAtAsc(stock)).thenReturn(histories);

            // When - Calcular tendencia
            boolean result = stockHistoryCalculateTrendService.execute(stock);

            // Then - Debe retornar true (tendencia alcista)
            // Motivo: Verificar que cuando la suma de variaciones es positiva, la tendencia es alcista
            verify(stockHistoryRepository).findTop10ByStockOrderByCreatedAtAsc(stock);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Given stock with negative variations sum When calculating trend Then returns false (bearish)")
        void whenNegativeVariationsSum_returnsFalse() {
            // Given - Acción con suma de variaciones negativa
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory history1 = InvestmentTestMother.stockHistoryWithVariation(1201L, -5.0,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE - 5.0);
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, -3.0,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE - 8.0);
            List<StockHistory> histories = Arrays.asList(history1, history2);
            // Suma de variaciones: -5.0 + (-3.0) = -8.0 < 0

            when(stockHistoryRepository.findTop10ByStockOrderByCreatedAtAsc(stock)).thenReturn(histories);

            // When - Calcular tendencia
            boolean result = stockHistoryCalculateTrendService.execute(stock);

            // Then - Debe retornar false (tendencia bajista)
            // Motivo: Verificar que cuando la suma de variaciones es negativa, la tendencia es bajista
            verify(stockHistoryRepository).findTop10ByStockOrderByCreatedAtAsc(stock);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Given stock with zero variations sum When calculating trend Then returns false")
        void whenZeroVariationsSum_returnsFalse() {
            // Given - Acción con suma de variaciones cero
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory history1 = InvestmentTestMother.stockHistoryWithVariation(1201L, 5.0,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0);
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, -5.0,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE);
            List<StockHistory> histories = Arrays.asList(history1, history2);
            // Suma de variaciones: 5.0 + (-5.0) = 0.0

            when(stockHistoryRepository.findTop10ByStockOrderByCreatedAtAsc(stock)).thenReturn(histories);

            // When - Calcular tendencia
            boolean result = stockHistoryCalculateTrendService.execute(stock);

            // Then - Debe retornar false (suma cero o negativa)
            // Motivo: Verificar que cuando la suma de variaciones es cero, la tendencia no es alcista
            verify(stockHistoryRepository).findTop10ByStockOrderByCreatedAtAsc(stock);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Given stock with no history When calculating trend Then returns false")
        void whenNoHistory_returnsFalse() {
            // Given - Acción sin historial
            Stock stock = InvestmentTestMother.defaultStock();
            List<StockHistory> histories = Collections.emptyList();

            when(stockHistoryRepository.findTop10ByStockOrderByCreatedAtAsc(stock)).thenReturn(histories);

            // When - Calcular tendencia
            boolean result = stockHistoryCalculateTrendService.execute(stock);

            // Then - Debe retornar false (sin historial)
            // Motivo: Verificar que cuando no hay historial, la tendencia no es alcista (caso límite)
            verify(stockHistoryRepository).findTop10ByStockOrderByCreatedAtAsc(stock);
            assertThat(result).isFalse();
        }
    }
}

