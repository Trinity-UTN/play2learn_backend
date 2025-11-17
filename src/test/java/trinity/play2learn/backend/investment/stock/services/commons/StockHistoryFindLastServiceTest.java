package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;

@ExtendWith(MockitoExtension.class)
class StockHistoryFindLastServiceTest {

    @Mock
    private IStockHistoryRepository stockHistoryRepository;

    private StockHistoryFindLastService stockHistoryFindLastService;

    @BeforeEach
    void setUp() {
        stockHistoryFindLastService = new StockHistoryFindLastService(stockHistoryRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stock with history When finding last history Then returns most recent history")
        void whenStockWithHistory_returnsMostRecentHistory() {
            // Given - Acción con historial
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory expectedHistory = InvestmentTestMother.defaultStockHistory();

            when(stockHistoryRepository.findTopByStockOrderByCreatedAtDesc(stock))
                    .thenReturn(Optional.of(expectedHistory));

            // When - Buscar último historial
            StockHistory result = stockHistoryFindLastService.execute(stock);

            // Then - Debe retornar historial más reciente
            verify(stockHistoryRepository).findTopByStockOrderByCreatedAtDesc(stock);
            assertThat(result).isEqualTo(expectedHistory);
        }

        @Test
        @DisplayName("Given stock with no history When finding last history Then throws NotFoundException")
        void whenNoHistory_throwsNotFoundException() {
            // Given - Acción sin historial
            Stock stock = InvestmentTestMother.defaultStock();

            when(stockHistoryRepository.findTopByStockOrderByCreatedAtDesc(stock))
                    .thenReturn(Optional.empty());

            // When & Then - Debe lanzar NotFoundException
            assertThatThrownBy(() -> stockHistoryFindLastService.execute(stock))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No se encontro el stock");

            verify(stockHistoryRepository).findTopByStockOrderByCreatedAtDesc(stock);
        }
    }
}

