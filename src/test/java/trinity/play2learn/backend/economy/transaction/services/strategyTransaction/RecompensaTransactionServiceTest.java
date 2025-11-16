package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityRemoveBalanceService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;

@ExtendWith(MockitoExtension.class)
class RecompensaTransactionServiceTest {

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private IWalletAddAmountService addAmountWalletService;

    @Mock
    private IActivityRemoveBalanceService activityRemoveBalanceService;

    @Mock
    private IReserveFindLastService findLastReserveService;

    private RecompensaTransactionService recompensaTransactionService;

    @BeforeEach
    void setUp() {
        recompensaTransactionService = new RecompensaTransactionService(
            transaccionRepository,
            addAmountWalletService,
            activityRemoveBalanceService,
            findLastReserveService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given activity with sufficient balance When executing recompensa transaction Then creates transaction, removes amount from activity and adds to wallet")
        void whenActivityHasSufficientBalance_createsTransactionRemovesFromActivityAndAddsToWallet() {
            // Given - Actividad con balance suficiente
            Activity activity = EconomyTestMother.defaultActivity();
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double amount = 100.0;
            String description = "Recompensa por actividad completada";
            TransactionActor origin = TransactionActor.SISTEMA;
            TransactionActor destination = TransactionActor.ESTUDIANTE;
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(EconomyTestMother.defaultReserve());
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de recompensa
            Transaction result = recompensaTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null,
                activity,
                null, null, null, null
            );

            // Then - Debe crear transacción, remover monto de actividad y agregar al wallet
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(activityRemoveBalanceService).execute(activity, amount);
            verify(addAmountWalletService).execute(wallet, amount);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given activity with insufficient balance When executing recompensa transaction Then throws ConflictException")
        void whenActivityHasInsufficientBalance_throwsConflictException() {
            // Given - Actividad con balance insuficiente
            Activity activity = EconomyTestMother.activity(EconomyTestMother.DEFAULT_ACTIVITY_ID, 50.0);
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double amount = 100.0;

            // When & Then - Debe lanzar ConflictException
            assertThatThrownBy(() -> recompensaTransactionService.execute(
                amount,
                "Recompensa por actividad completada",
                TransactionActor.SISTEMA,
                TransactionActor.ESTUDIANTE,
                wallet,
                null,
                activity,
                null, null, null, null
            ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_SUBJECT);
        }

        @Test
        @DisplayName("Given activity with balance equal to amount When executing recompensa transaction Then creates transaction successfully")
        void whenActivityBalanceEqualsAmount_createsTransactionSuccessfully() {
            // Given - Actividad con balance igual al monto
            Activity activity = EconomyTestMother.activity(EconomyTestMother.DEFAULT_ACTIVITY_ID, 100.0);
            Wallet wallet = EconomyTestMother.defaultWallet();
            Double amount = 100.0;
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(EconomyTestMother.defaultReserve());
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de recompensa
            Transaction result = recompensaTransactionService.execute(
                amount,
                "Recompensa por actividad completada",
                TransactionActor.SISTEMA,
                TransactionActor.ESTUDIANTE,
                wallet,
                null,
                activity,
                null, null, null, null
            );

            // Then - Debe crear transacción exitosamente
            assertThat(result).isEqualTo(savedTransaction);
        }
    }
}

