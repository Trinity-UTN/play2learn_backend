package trinity.play2learn.backend.economy.reserve.services.commons;

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
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.repositories.IReserveRepository;

@ExtendWith(MockitoExtension.class)
class ReserveFindLastServiceTest {

    @Mock
    private IReserveRepository reserveRepository;

    private ReserveFindLastService reserveFindLastService;

    @BeforeEach
    void setUp() {
        reserveFindLastService = new ReserveFindLastService(reserveRepository);
    }

    @Nested
    @DisplayName("get")
    class Get {

        @Test
        @DisplayName("Given existing reserve When getting last reserve Then returns reserve")
        void whenReserveExists_returnsReserve() {
            // Given - Reserva existente
            Reserve expectedReserve = EconomyTestMother.defaultReserve();

            when(reserveRepository.findFirstByOrderByCreatedAtDesc())
                .thenReturn(Optional.of(expectedReserve));

            // When - Obtener última reserva
            Reserve result = reserveFindLastService.get();

            // Then - Debe retornar reserva
            verify(reserveRepository).findFirstByOrderByCreatedAtDesc();

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(expectedReserve.getId());
            assertThat(result.getReserveBalance()).isEqualTo(expectedReserve.getReserveBalance());
            assertThat(result.getCirculationBalance()).isEqualTo(expectedReserve.getCirculationBalance());
            assertThat(result.getInitialBalance()).isEqualTo(expectedReserve.getInitialBalance());
        }

        @Test
        @DisplayName("Given no existing reserve When getting last reserve Then throws NotFoundException")
        void whenNoReserveExists_throwsNotFoundException() {
            // Given - No existe reserva
            when(reserveRepository.findFirstByOrderByCreatedAtDesc())
                .thenReturn(Optional.empty());

            // When & Then - Debe lanzar NotFoundException
            assertThatThrownBy(() -> reserveFindLastService.get())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Reserva con id ultima no encontrado");

            verify(reserveRepository).findFirstByOrderByCreatedAtDesc();
        }

        @Test
        @DisplayName("Given reserve with specific balances When getting last reserve Then returns reserve with correct balances")
        void whenReserveWithSpecificBalances_returnsReserveWithCorrectBalances() {
            // Given - Reserva con balances específicos
            Reserve expectedReserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                EconomyTestMother.DEFAULT_RESERVE_BALANCE,
                EconomyTestMother.DEFAULT_CIRCULATION_BALANCE
            );

            when(reserveRepository.findFirstByOrderByCreatedAtDesc())
                .thenReturn(Optional.of(expectedReserve));

            // When - Obtener última reserva
            Reserve result = reserveFindLastService.get();

            // Then - Debe retornar reserva con balances correctos
            assertThat(result).isNotNull();
            assertThat(result.getReserveBalance()).isEqualTo(EconomyTestMother.DEFAULT_RESERVE_BALANCE);
            assertThat(result.getCirculationBalance()).isEqualTo(EconomyTestMother.DEFAULT_CIRCULATION_BALANCE);
        }
    }
}

