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

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddBalanceService;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;

@ExtendWith(MockitoExtension.class)
class AsignacionTransactionServiceTest {

    @Mock
    private IReserveFindLastService findLastReserveService;

    @Mock
    private ISubjectAddBalanceService subjectAddBalanceService;

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private IReserveModifyService modifyReserveService;

    private AsignacionTransactionService asignacionTransactionService;

    @BeforeEach
    void setUp() {
        asignacionTransactionService = new AsignacionTransactionService(
            findLastReserveService,
            subjectAddBalanceService,
            transaccionRepository,
            modifyReserveService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given subject with correct assign amount When executing asignacion transaction Then creates transaction, adds balance to subject and moves to circulation")
        void whenSubjectWithCorrectAssignAmount_createsTransactionAddsBalanceAndMovesToCirculation() {
            // Given - Materia con monto de asignación correcto
            Subject subject = EconomyTestMother.defaultSubject();
            Double amount = subject.getInitialBalance() - subject.getActualBalance();
            String description = "Asignación de monedas mensual.";
            TransactionActor origin = TransactionActor.SISTEMA;
            TransactionActor destination = TransactionActor.ESTUDIANTE;
            Reserve reserve = EconomyTestMother.defaultReserve();
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de asignación
            Transaction result = asignacionTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                null,
                subject,
                null, null, null, null, null
            );

            // Then - Debe crear transacción, agregar balance a materia y mover a circulación
            verify(findLastReserveService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(subjectAddBalanceService).execute(subject, amount);
            verify(modifyReserveService).moveToCirculation(amount, reserve);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given amount not equal to assign amount When executing asignacion transaction Then throws UnsupportedOperationException")
        void whenAmountNotEqualAssignAmount_throwsUnsupportedOperationException() {
            // Given - Monto que no coincide con monto de asignación
            Subject subject = EconomyTestMother.defaultSubject();
            Double assignAmount = subject.getInitialBalance() - subject.getActualBalance();
            Double amount = assignAmount + 100.0;

            // When & Then - Debe lanzar UnsupportedOperationException
            assertThatThrownBy(() -> asignacionTransactionService.execute(
                amount,
                "Asignación de monedas mensual.",
                TransactionActor.SISTEMA,
                TransactionActor.ESTUDIANTE,
                null,
                subject,
                null, null, null, null, null
            ))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage(EconomyMessages.INCORRECT_AMOUNT);
        }

        @Test
        @DisplayName("Given reserve with insufficient balance When executing asignacion transaction Then adjusts reserve and creates transaction")
        void whenReserveWithInsufficientBalance_adjustsReserveAndCreatesTransaction() {
            // Given - Reserva con balance insuficiente
            Subject subject = EconomyTestMother.defaultSubject();
            Double amount = subject.getInitialBalance() - subject.getActualBalance();
            Reserve reserve = EconomyTestMother.reserveWithBalances(
                EconomyTestMother.DEFAULT_RESERVE_ID,
                100.0,
                10000.0
            );
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(findLastReserveService.get()).thenReturn(reserve);
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de asignación
            Transaction result = asignacionTransactionService.execute(
                amount,
                "Asignación de monedas mensual.",
                TransactionActor.SISTEMA,
                TransactionActor.ESTUDIANTE,
                null,
                subject,
                null, null, null, null, null
            );

            // Then - Debe ajustar reserva y crear transacción
            assertThat(result).isEqualTo(savedTransaction);
        }
    }
}

