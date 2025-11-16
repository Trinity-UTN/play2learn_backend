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
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;

@ExtendWith(MockitoExtension.class)
class StockFindByIdServiceTest {

    @Mock
    private IStockRepository stockRepository;

    private StockFindByIdService stockFindByIdService;

    @BeforeEach
    void setUp() {
        stockFindByIdService = new StockFindByIdService(stockRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given valid stock id When finding by id Then returns stock")
        void whenValidId_returnsStock() {
            // Given - ID válido
            Long id = InvestmentTestMother.DEFAULT_STOCK_ID;
            Stock expectedStock = InvestmentTestMother.defaultStock();

            when(stockRepository.findById(id)).thenReturn(Optional.of(expectedStock));

            // When - Buscar por ID
            Stock result = stockFindByIdService.execute(id);

            // Then - Debe retornar acción
            // Motivo: Verificar que el servicio retorna la entidad correcta y que el ID coincide
            verify(stockRepository).findById(id);
            assertThat(result)
                .isEqualTo(expectedStock)
                .extracting(Stock::getId)
                .isEqualTo(id);
        }

        @Test
        @DisplayName("Given non-existing stock id When finding by id Then throws NotFoundException")
        void whenNonExistingId_throwsNotFoundException() {
            // Given - ID no existente
            // Motivo: Validar que el servicio maneja correctamente IDs que no existen en la base de datos
            Long id = 999L;

            when(stockRepository.findById(id)).thenReturn(Optional.empty());

            // When & Then - Debe lanzar NotFoundException
            assertThatThrownBy(() -> stockFindByIdService.execute(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Accion no encontrada");

            verify(stockRepository).findById(id);
        }
    }
}

