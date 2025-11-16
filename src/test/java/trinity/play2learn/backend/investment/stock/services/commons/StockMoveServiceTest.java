package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;

@ExtendWith(MockitoExtension.class)
class StockMoveServiceTest {

    @Mock
    private IStockRepository stockRepository;

    private StockMoveService stockMoveService;

    @BeforeEach
    void setUp() {
        stockMoveService = new StockMoveService(stockRepository);
    }

    @Nested
    @DisplayName("toSold")
    class ToSold {

        @Test
        @DisplayName("Given stock with sufficient available amount When moving to sold Then decrements available and increments sold")
        void whenSufficientAvailable_decrementsAvailableAndIncrementsSold() {
            // Given - Acción con cantidad disponible suficiente
            Stock stock = InvestmentTestMother.defaultStock();
            BigInteger quantity = InvestmentTestMother.DEFAULT_QUANTITY;
            BigInteger expectedAvailable = stock.getAvailableAmount().subtract(quantity);
            BigInteger expectedSold = stock.getSoldAmount().add(quantity);

            when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When - Mover a vendidas
            stockMoveService.toSold(stock, quantity);

            // Then - Debe decrementar disponible e incrementar vendidas
            // Motivo: Verificar que el movimiento de acciones actualiza correctamente los contadores
            ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
            verify(stockRepository).save(stockCaptor.capture());

            Stock capturedStock = stockCaptor.getValue();
            assertThat(capturedStock)
                .extracting(Stock::getAvailableAmount, Stock::getSoldAmount)
                .containsExactly(expectedAvailable, expectedSold);
        }

        @Test
        @DisplayName("Given stock with insufficient available amount When moving to sold Then throws BadRequestException")
        void whenInsufficientAvailable_throwsBadRequestException() {
            // Given - Acción con cantidad disponible insuficiente
            // Motivo: Validar que el servicio previene movimientos inválidos que resultarían en valores negativos
            Stock stock = InvestmentTestMother.stockWithAvailability(InvestmentTestMother.DEFAULT_STOCK_ID,
                    BigInteger.valueOf(5), InvestmentTestMother.DEFAULT_SOLD_AMOUNT);
            BigInteger quantity = InvestmentTestMother.DEFAULT_QUANTITY;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> stockMoveService.toSold(stock, quantity))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No hay acciones disponibles suficientes para mover a vendidas.");

            // Verify - No debe guardar cuando hay excepción
            verify(stockRepository, org.mockito.Mockito.never()).save(any(Stock.class));
        }
    }

    @Nested
    @DisplayName("toAvailable")
    class ToAvailable {

        @Test
        @DisplayName("Given stock with sufficient sold amount When moving to available Then decrements sold and increments available")
        void whenSufficientSold_decrementsSoldAndIncrementsAvailable() {
            // Given - Acción con cantidad vendida suficiente
            Stock stock = InvestmentTestMother.defaultStock();
            BigInteger quantity = BigInteger.valueOf(5);
            BigInteger expectedSold = stock.getSoldAmount().subtract(quantity);
            BigInteger expectedAvailable = stock.getAvailableAmount().add(quantity);

            when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When - Mover a disponibles
            stockMoveService.toAvailable(stock, quantity);

            // Then - Debe decrementar vendidas e incrementar disponibles
            // Motivo: Verificar que el movimiento de acciones actualiza correctamente los contadores
            ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
            verify(stockRepository).save(stockCaptor.capture());

            Stock capturedStock = stockCaptor.getValue();
            assertThat(capturedStock)
                .extracting(Stock::getSoldAmount, Stock::getAvailableAmount)
                .containsExactly(expectedSold, expectedAvailable);
        }

        @Test
        @DisplayName("Given stock with insufficient sold amount When moving to available Then throws BadRequestException")
        void whenInsufficientSold_throwsBadRequestException() {
            // Given - Acción con cantidad vendida insuficiente
            // Motivo: Validar que el servicio previene movimientos inválidos que resultarían en valores negativos
            Stock stock = InvestmentTestMother.stockWithAvailability(InvestmentTestMother.DEFAULT_STOCK_ID,
                    InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT, BigInteger.valueOf(5));
            BigInteger quantity = InvestmentTestMother.DEFAULT_QUANTITY;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> stockMoveService.toAvailable(stock, quantity))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No hay acciones vendidas suficientes para mover a disponibles.");

            // Verify - No debe guardar cuando hay excepción
            verify(stockRepository, org.mockito.Mockito.never()).save(any(Stock.class));
        }
    }
}

