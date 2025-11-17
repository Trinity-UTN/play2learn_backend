package trinity.play2learn.backend.economy.transaction.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@ExtendWith(MockitoExtension.class)
class TransactionGenerateServiceTest {

    @Mock
    private ITransactionStrategyService mockStrategy;

    private Map<String, ITransactionStrategyService> strategies;
    private TransactionGenerateService transactionGenerateService;

    @BeforeEach
    void setUp() {
        strategies = new HashMap<>();
        strategies.put(TypeTransaction.COMPRA.name(), mockStrategy);
        transactionGenerateService = new TransactionGenerateService(strategies);
    }

    @Nested
    @DisplayName("generate")
    class Generate {

        @Test
        @DisplayName("Given valid type transaction and positive amount When generating Then delegates to strategy and returns transaction")
        void whenValidTypeAndPositiveAmount_delegatesToStrategyAndReturnsTransaction() {
            // Given - Tipo de transacción válido y monto positivo
            TypeTransaction type = TypeTransaction.COMPRA;
            Double amount = EconomyTestMother.AMOUNT_MEDIUM;
            String description = "Compra de beneficio";
            TransactionActor origin = TransactionActor.ESTUDIANTE;
            TransactionActor destination = TransactionActor.SISTEMA;
            Wallet wallet = EconomyTestMother.defaultWallet();
            Transaction expectedTransaction = EconomyTestMother.defaultTransaction();

            when(mockStrategy.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null, null
            )).thenReturn(expectedTransaction);

            // When - Generar transacción
            Transaction result = transactionGenerateService.generate(
                type,
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null, null
            );

            // Then - Debe delegar a estrategia y retornar transacción
            verify(mockStrategy).execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null, null
            );

            assertThat(result).isEqualTo(expectedTransaction);
        }

        @Test
        @DisplayName("Given zero amount When generating Then throws ConflictException")
        void whenAmountIsZero_throwsConflictException() {
            // Given - Tipo de transacción válido y monto cero
            TypeTransaction type = TypeTransaction.COMPRA;
            Double amount = EconomyTestMother.AMOUNT_ZERO;

            // When & Then - Debe lanzar ConflictException con mensaje AMOUNT_MAJOR_TO_0
            assertThatThrownBy(() -> transactionGenerateService.generate(
                type,
                amount,
                "Description",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                EconomyTestMother.defaultWallet(),
                null, null, null, null, null, null
            ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(EconomyMessages.AMOUNT_MAJOR_TO_0);
        }

        @Test
        @DisplayName("Given negative amount When generating Then throws ConflictException")
        void whenAmountIsNegative_throwsConflictException() {
            // Given - Tipo de transacción válido y monto negativo
            TypeTransaction type = TypeTransaction.COMPRA;
            Double amount = EconomyTestMother.AMOUNT_NEGATIVE;

            // When & Then - Debe lanzar ConflictException con mensaje AMOUNT_MAJOR_TO_0
            assertThatThrownBy(() -> transactionGenerateService.generate(
                type,
                amount,
                "Description",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                EconomyTestMother.defaultWallet(),
                null, null, null, null, null, null
            ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(EconomyMessages.AMOUNT_MAJOR_TO_0);
        }

        @Test
        @DisplayName("Given unsupported transaction type When generating Then throws ConflictException")
        void whenUnsupportedTransactionType_throwsConflictException() {
            // Given - Tipo de transacción no soportado
            TypeTransaction type = TypeTransaction.INVERSION;
            Double amount = EconomyTestMother.AMOUNT_MEDIUM;

            // When & Then - Debe lanzar ConflictException con mensaje de tipo no soportado
            assertThatThrownBy(() -> transactionGenerateService.generate(
                type,
                amount,
                "Description",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                EconomyTestMother.defaultWallet(),
                null, null, null, null, null, null
            ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(EconomyMessages.getTransactionNotSupported(type.name()));
        }

        @Test
        @DisplayName("Given valid type and minimum amount (0.01) When generating Then delegates to strategy successfully")
        void whenValidTypeAndMinimumAmount_delegatesToStrategySuccessfully() {
            // Given - Tipo de transacción válido y monto mínimo
            TypeTransaction type = TypeTransaction.COMPRA;
            Double amount = EconomyTestMother.AMOUNT_MINIMUM;
            String description = "Compra de beneficio";
            TransactionActor origin = TransactionActor.ESTUDIANTE;
            TransactionActor destination = TransactionActor.SISTEMA;
            Wallet wallet = EconomyTestMother.defaultWallet();
            Transaction expectedTransaction = EconomyTestMother.defaultTransaction();

            when(mockStrategy.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null, null
            )).thenReturn(expectedTransaction);

            // When - Generar transacción con monto mínimo
            Transaction result = transactionGenerateService.generate(
                type,
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null, null
            );

            // Then - Debe delegar a estrategia exitosamente
            assertThat(result).isEqualTo(expectedTransaction);
        }
    }
}

