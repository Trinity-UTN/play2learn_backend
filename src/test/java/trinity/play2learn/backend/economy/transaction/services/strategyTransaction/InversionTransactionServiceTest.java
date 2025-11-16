package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;

@ExtendWith(MockitoExtension.class)
class InversionTransactionServiceTest {

    private InversionTransactionService inversionTransactionService;

    @BeforeEach
    void setUp() {
        inversionTransactionService = new InversionTransactionService();
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given any parameters When executing inversion transaction Then throws UnsupportedOperationException")
        void whenAnyParameters_throwsUnsupportedOperationException() {
            // Given - Cualquier parámetro (el servicio no está implementado)
            Double amount = 100.0;

            // When & Then - Debe lanzar UnsupportedOperationException
            assertThatThrownBy(() -> inversionTransactionService.execute(
                amount,
                "Inversión",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                EconomyTestMother.defaultWallet(),
                null, null, null, null, null, null
            ))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Unimplemented method 'execute'");
        }
    }
}

