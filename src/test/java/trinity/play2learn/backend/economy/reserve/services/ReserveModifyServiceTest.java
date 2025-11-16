package trinity.play2learn.backend.economy.reserve.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;

@ExtendWith(MockitoExtension.class)
class ReserveModifyServiceTest {

    @Mock
    private IReserveRepository reserveRepository;

    private ReserveModifyService reserveModifyService;

    @BeforeEach
    void setUp() {
        reserveModifyService = new ReserveModifyService(reserveRepository);
    }

    @Nested
    @DisplayName("moveToReserve")
    class MoveToReserve {

        @Test
        @DisplayName("Given reserve with sufficient circulation balance and amount When moving to reserve Then transfers amount and persists reserve")
        void whenReserveWithSufficientCirculationBalance_transfersAmountAndPersists() {
            // Given - Reserva con balance de circulación suficiente y monto a transferir
            Reserve reserve = EconomyTestMother.defaultReserve();
            Double amount = EconomyTestMother.AMOUNT_LARGE;
            Double expectedCirculationBalance = reserve.getCirculationBalance() - amount;
            Double expectedReserveBalance = reserve.getReserveBalance() + amount;

            Reserve savedReserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                expectedReserveBalance,
                expectedCirculationBalance
            );
            when(reserveRepository.save(any(Reserve.class))).thenReturn(savedReserve);

            // When - Mover monto de circulación a reserva
            Reserve result = reserveModifyService.moveToReserve(amount, reserve);

            // Then - Debe transferir monto y persistir reserva
            ArgumentCaptor<Reserve> reserveCaptor = ArgumentCaptor.forClass(Reserve.class);
            verify(reserveRepository).save(reserveCaptor.capture());

            Reserve capturedReserve = reserveCaptor.getValue();
            assertThat(capturedReserve.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
            assertThat(capturedReserve.getReserveBalance()).isEqualTo(expectedReserveBalance);
            assertThat(capturedReserve.getLastUpdateAt()).isNotNull();
            assertThat(result.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
            assertThat(result.getReserveBalance()).isEqualTo(expectedReserveBalance);
        }

        @Test
        @DisplayName("Given reserve and zero amount When moving to reserve Then throws BadRequestException")
        void whenAmountIsZero_throwsBadRequestException() {
            // Given - Reserva y monto cero
            Reserve reserve = EconomyTestMother.defaultReserve();
            Double amount = EconomyTestMother.AMOUNT_ZERO;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> reserveModifyService.moveToReserve(amount, reserve))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El monto debe ser mayor a 0");

            verify(reserveRepository, never()).save(any(Reserve.class));
        }

        @Test
        @DisplayName("Given reserve and negative amount When moving to reserve Then throws BadRequestException")
        void whenAmountIsNegative_throwsBadRequestException() {
            // Given - Reserva y monto negativo
            Reserve reserve = EconomyTestMother.defaultReserve();
            Double amount = EconomyTestMother.AMOUNT_NEGATIVE;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> reserveModifyService.moveToReserve(amount, reserve))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El monto debe ser mayor a 0");

            verify(reserveRepository, never()).save(any(Reserve.class));
        }

        @Test
        @DisplayName("Given reserve with insufficient circulation balance When moving to reserve Then throws BadRequestException")
        void whenReserveWithInsufficientCirculationBalance_throwsBadRequestException() {
            // Given - Reserva con balance de circulación insuficiente
            Reserve reserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                EconomyTestMother.DEFAULT_RESERVE_BALANCE,
                EconomyTestMother.AMOUNT_SMALL
            );
            Double amount = EconomyTestMother.AMOUNT_LARGE;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> reserveModifyService.moveToReserve(amount, reserve))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No hay suficiente dinero en la circulacion");

            verify(reserveRepository, never()).save(any(Reserve.class));
        }

        @Test
        @DisplayName("Given reserve with circulation balance equal to amount When moving to reserve Then transfers amount successfully")
        void whenReserveCirculationBalanceEqualsAmount_transfersAmountSuccessfully() {
            // Given - Reserva con balance de circulación igual al monto
            Reserve reserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                EconomyTestMother.DEFAULT_RESERVE_BALANCE,
                EconomyTestMother.AMOUNT_LARGE
            );
            Double amount = EconomyTestMother.AMOUNT_LARGE;
            Double expectedCirculationBalance = EconomyTestMother.AMOUNT_ZERO;
            Double expectedReserveBalance = EconomyTestMother.DEFAULT_RESERVE_BALANCE + EconomyTestMother.AMOUNT_LARGE;

            Reserve savedReserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                expectedReserveBalance,
                expectedCirculationBalance
            );
            when(reserveRepository.save(any(Reserve.class))).thenReturn(savedReserve);

            // When - Mover monto igual al balance de circulación
            Reserve result = reserveModifyService.moveToReserve(amount, reserve);

            // Then - Debe transferir monto exitosamente
            assertThat(result.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
            assertThat(result.getReserveBalance()).isEqualTo(expectedReserveBalance);
        }
    }

    @Nested
    @DisplayName("moveToCirculation")
    class MoveToCirculation {

        @Test
        @DisplayName("Given reserve with sufficient reserve balance and amount When moving to circulation Then transfers amount and persists reserve")
        void whenReserveWithSufficientReserveBalance_transfersAmountAndPersists() {
            // Given - Reserva con balance de reserva suficiente y monto a transferir
            Reserve reserve = EconomyTestMother.defaultReserve();
            Double amount = EconomyTestMother.AMOUNT_LARGE;
            Double expectedReserveBalance = reserve.getReserveBalance() - amount;
            Double expectedCirculationBalance = reserve.getCirculationBalance() + amount;

            Reserve savedReserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                expectedReserveBalance,
                expectedCirculationBalance
            );
            when(reserveRepository.save(any(Reserve.class))).thenReturn(savedReserve);

            // When - Mover monto de reserva a circulación
            Reserve result = reserveModifyService.moveToCirculation(amount, reserve);

            // Then - Debe transferir monto y persistir reserva
            ArgumentCaptor<Reserve> reserveCaptor = ArgumentCaptor.forClass(Reserve.class);
            verify(reserveRepository).save(reserveCaptor.capture());

            Reserve capturedReserve = reserveCaptor.getValue();
            assertThat(capturedReserve.getReserveBalance()).isEqualTo(expectedReserveBalance);
            assertThat(capturedReserve.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
            // Nota: moveToCirculation no establece lastUpdateAt, solo moveToReserve lo hace
            assertThat(result.getReserveBalance()).isEqualTo(expectedReserveBalance);
            assertThat(result.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
        }

        @Test
        @DisplayName("Given reserve and zero amount When moving to circulation Then throws BadRequestException")
        void whenAmountIsZero_throwsBadRequestException() {
            // Given - Reserva y monto cero
            Reserve reserve = EconomyTestMother.defaultReserve();
            Double amount = EconomyTestMother.AMOUNT_ZERO;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> reserveModifyService.moveToCirculation(amount, reserve))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(EconomyMessages.AMOUNT_MAJOR_TO_0);

            verify(reserveRepository, never()).save(any(Reserve.class));
        }

        @Test
        @DisplayName("Given reserve and negative amount When moving to circulation Then throws BadRequestException")
        void whenAmountIsNegative_throwsBadRequestException() {
            // Given - Reserva y monto negativo
            Reserve reserve = EconomyTestMother.defaultReserve();
            Double amount = EconomyTestMother.AMOUNT_NEGATIVE;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> reserveModifyService.moveToCirculation(amount, reserve))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(EconomyMessages.AMOUNT_MAJOR_TO_0);

            verify(reserveRepository, never()).save(any(Reserve.class));
        }

        @Test
        @DisplayName("Given reserve with insufficient reserve balance When moving to circulation Then adjusts reserve and transfers amount")
        void whenReserveWithInsufficientReserveBalance_adjustsReserveAndTransfersAmount() {
            // Given - Reserva con balance de reserva insuficiente
            Reserve reserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                EconomyTestMother.AMOUNT_SMALL,
                EconomyTestMother.DEFAULT_CIRCULATION_BALANCE
            );
            Double amount = EconomyTestMother.AMOUNT_LARGE;
            Double expectedReserveBalance = EconomyTestMother.AMOUNT_ZERO;
            Double expectedCirculationBalance = reserve.getCirculationBalance() + amount;

            Reserve savedReserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                expectedReserveBalance,
                expectedCirculationBalance
            );
            when(reserveRepository.save(any(Reserve.class))).thenReturn(savedReserve);

            // When - Mover monto mayor al balance de reserva
            Reserve result = reserveModifyService.moveToCirculation(amount, reserve);

            // Then - Debe ajustar reserva creando diferencia faltante y transferir monto
            ArgumentCaptor<Reserve> reserveCaptor = ArgumentCaptor.forClass(Reserve.class);
            verify(reserveRepository).save(reserveCaptor.capture());

            Reserve capturedReserve = reserveCaptor.getValue();
            // Primero se ajusta la reserva (suma diferencia faltante), luego se resta el monto
            assertThat(capturedReserve.getReserveBalance()).isEqualTo(0.0);
            assertThat(capturedReserve.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
            assertThat(result.getReserveBalance()).isEqualTo(expectedReserveBalance);
            assertThat(result.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
        }

        @Test
        @DisplayName("Given reserve with reserve balance equal to amount When moving to circulation Then transfers amount successfully")
        void whenReserveBalanceEqualsAmount_transfersAmountSuccessfully() {
            // Given - Reserva con balance de reserva igual al monto
            Reserve reserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                EconomyTestMother.AMOUNT_LARGE,
                EconomyTestMother.DEFAULT_CIRCULATION_BALANCE
            );
            Double amount = EconomyTestMother.AMOUNT_LARGE;
            Double expectedReserveBalance = EconomyTestMother.AMOUNT_ZERO;
            Double expectedCirculationBalance = EconomyTestMother.DEFAULT_CIRCULATION_BALANCE + EconomyTestMother.AMOUNT_LARGE;

            Reserve savedReserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                expectedReserveBalance,
                expectedCirculationBalance
            );
            when(reserveRepository.save(any(Reserve.class))).thenReturn(savedReserve);

            // When - Mover monto igual al balance de reserva
            Reserve result = reserveModifyService.moveToCirculation(amount, reserve);

            // Then - Debe transferir monto exitosamente
            assertThat(result.getReserveBalance()).isEqualTo(expectedReserveBalance);
            assertThat(result.getCirculationBalance()).isEqualTo(expectedCirculationBalance);
        }
    }
}

