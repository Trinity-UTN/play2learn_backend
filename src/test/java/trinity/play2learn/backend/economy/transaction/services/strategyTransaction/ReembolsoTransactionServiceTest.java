package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import static org.assertj.core.api.Assertions.assertThat;
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

import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;

@ExtendWith(MockitoExtension.class)
class ReembolsoTransactionServiceTest {

    @Mock
    private IReserveFindLastService findLastReserveService;

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private IWalletAddAmountService addAmountWalletService;

    @Mock
    private IReserveModifyService modifyReserveService;

    private ReembolsoTransactionService reembolsoTransactionService;

    @BeforeEach
    void setUp() {
        reembolsoTransactionService = new ReembolsoTransactionService(
            findLastReserveService,
            transaccionRepository,
            addAmountWalletService,
            modifyReserveService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet and benefit When executing reembolso transaction Then creates transaction, adds amount to wallet and moves to circulation")
        void whenWalletAndBenefit_createsTransactionAddsToWalletAndMovesToCirculation() {
            // Given - Wallet y beneficio
            Wallet wallet = EconomyTestMother.defaultWallet();
            Benefit benefit = EconomyTestMother.benefit(EconomyTestMother.DEFAULT_BENEFIT_ID);
            Double amount = 100.0;
            String description = "Reembolso de beneficio";
            TransactionActor origin = TransactionActor.SISTEMA;
            TransactionActor destination = TransactionActor.ESTUDIANTE;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de reembolso
            Transaction result = reembolsoTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                wallet,
                null,
                null,
                benefit,
                null, null, null
            );

            // Then - Debe crear transacción, agregar monto al wallet y mover a circulación
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(addAmountWalletService).execute(wallet, amount);
            verify(modifyReserveService).moveToCirculation(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }
    }
}

