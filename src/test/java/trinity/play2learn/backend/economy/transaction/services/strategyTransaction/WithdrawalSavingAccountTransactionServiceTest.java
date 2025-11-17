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

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

@ExtendWith(MockitoExtension.class)
class WithdrawalSavingAccountTransactionServiceTest {

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private IReserveModifyService modifyReserveService;

    @Mock
    private IReserveFindLastService findLastReserveService;

    @Mock
    private IWalletAddAmountService addAmountWalletService;

    private WithdrawalSavingAccountTransactionService withdrawalSavingAccountTransactionService;

    @BeforeEach
    void setUp() {
        withdrawalSavingAccountTransactionService = new WithdrawalSavingAccountTransactionService(
            transaccionRepository,
            modifyReserveService,
            findLastReserveService,
            addAmountWalletService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given saving account with sufficient balance and positive amount When executing withdrawal transaction Then creates transaction, adds to wallet and moves to circulation")
        void whenSavingAccountWithSufficientBalance_createsTransactionAddsToWalletAndMovesToCirculation() {
            // Given - Caja de ahorro con saldo suficiente y monto positivo
            Wallet wallet = EconomyTestMother.defaultWallet();
            SavingAccount savingAccount = EconomyTestMother.defaultSavingAccount();
            Double amount = 100.0;
            String description = "Retiro de caja de ahorro";
            TransactionActor origin = TransactionActor.SISTEMA;
            TransactionActor destination = TransactionActor.ESTUDIANTE;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de retiro
            Transaction result = withdrawalSavingAccountTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null,
                savingAccount
            );

            // Then - Debe crear transacción, agregar al wallet y mover a circulación
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(addAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToCirculation(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given zero amount When executing withdrawal transaction Then throws BadRequestException")
        void whenAmountIsZero_throwsBadRequestException() {
            // Given - Monto cero
            Wallet wallet = EconomyTestMother.defaultWallet();
            SavingAccount savingAccount = EconomyTestMother.defaultSavingAccount();
            Double amount = 0.0;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> withdrawalSavingAccountTransactionService.execute(
                amount,
                "Retiro de caja de ahorro",
                TransactionActor.SISTEMA,
                TransactionActor.ESTUDIANTE,
                wallet,
                null, null, null, null, null,
                savingAccount
            ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El monto debe ser mayor a 0");
        }

        @Test
        @DisplayName("Given saving account with insufficient balance When executing withdrawal transaction Then throws BadRequestException")
        void whenSavingAccountWithInsufficientBalance_throwsBadRequestException() {
            // Given - Caja de ahorro con saldo insuficiente
            Wallet wallet = EconomyTestMother.defaultWallet();
            SavingAccount savingAccount = EconomyTestMother.savingAccount(
                EconomyTestMother.DEFAULT_SAVING_ACCOUNT_ID,
                50.0
            );
            Double amount = 100.0;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> withdrawalSavingAccountTransactionService.execute(
                amount,
                "Retiro de caja de ahorro",
                TransactionActor.SISTEMA,
                TransactionActor.ESTUDIANTE,
                wallet,
                null, null, null, null, null,
                savingAccount
            ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El monto es mayor al saldo actual de la caja de ahorro.");
        }
    }
}

