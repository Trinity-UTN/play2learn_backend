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
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

@ExtendWith(MockitoExtension.class)
class DepositSavingAccountTransactionServiceTest {

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private IWalletRemoveAmountService removeAmountWalletService;

    @Mock
    private IReserveModifyService modifyReserveService;

    @Mock
    private IReserveFindLastService findLastReserveService;

    private DepositSavingAccountTransactionService depositSavingAccountTransactionService;

    @BeforeEach
    void setUp() {
        depositSavingAccountTransactionService = new DepositSavingAccountTransactionService(
            transaccionRepository,
            removeAmountWalletService,
            modifyReserveService,
            findLastReserveService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with sufficient balance and positive amount When executing deposit transaction Then creates transaction, removes from wallet and moves to reserve")
        void whenWalletWithSufficientBalance_createsTransactionRemovesFromWalletAndMovesToReserve() {
            // Given - Wallet con balance suficiente y monto positivo
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 1000.0);
            SavingAccount savingAccount = EconomyTestMother.defaultSavingAccount();
            Double amount = 100.0;
            String description = "Depósito en caja de ahorro";
            TransactionActor origin = TransactionActor.ESTUDIANTE;
            TransactionActor destination = TransactionActor.SISTEMA;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
            when(removeAmountWalletService.execute(wallet, amount)).thenReturn(wallet);
            when(modifyReserveService.moveToReserve(amount, reserve)).thenReturn(reserve);

            // When - Ejecutar transacción de depósito
            Transaction result = depositSavingAccountTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null, null, null, null, null,
                savingAccount
            );

            // Then - Debe crear transacción, remover del wallet y mover a reserva
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(removeAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToReserve(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given zero amount When executing deposit transaction Then throws BadRequestException")
        void whenAmountIsZero_throwsBadRequestException() {
            // Given - Monto cero
            Wallet wallet = EconomyTestMother.defaultWallet();
            SavingAccount savingAccount = EconomyTestMother.defaultSavingAccount();
            Double amount = 0.0;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> depositSavingAccountTransactionService.execute(
                amount,
                "Depósito en caja de ahorro",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null, null, null,
                savingAccount
            ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El monto debe ser mayor a 0");
        }

        @Test
        @DisplayName("Given wallet with insufficient balance When executing deposit transaction Then throws BadRequestException")
        void whenWalletWithInsufficientBalance_throwsBadRequestException() {
            // Given - Wallet con balance insuficiente
            Wallet wallet = EconomyTestMother.walletWithBalance(EconomyTestMother.DEFAULT_WALLET_ID, 50.0);
            SavingAccount savingAccount = EconomyTestMother.defaultSavingAccount();
            Double amount = 100.0;

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> depositSavingAccountTransactionService.execute(
                amount,
                "Depósito en caja de ahorro",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null, null, null, null, null,
                savingAccount
            ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El wallet no cuenta con el balance suficiente para realizar el deposito en la caja de ahorro");
        }
    }
}

