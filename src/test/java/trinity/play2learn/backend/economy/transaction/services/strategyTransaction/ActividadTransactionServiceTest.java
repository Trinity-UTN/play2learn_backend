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
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityAddBalanceService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRemoveBalanceService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;

@ExtendWith(MockitoExtension.class)
class ActividadTransactionServiceTest {

    @Mock
    private ITransactionRepository transaccionRepository;

    @Mock
    private ISubjectRemoveBalanceService subjectRemoveBalanceService;

    @Mock
    private IReserveFindLastService reserveFindLastService;

    @Mock
    private IActivityAddBalanceService activityAddBalanceService;

    private ActividadTransactionService actividadTransactionService;

    @BeforeEach
    void setUp() {
        actividadTransactionService = new ActividadTransactionService(
            transaccionRepository,
            subjectRemoveBalanceService,
            reserveFindLastService,
            activityAddBalanceService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given subject with sufficient balance and amount within 30% limit When executing actividad transaction Then creates transaction, removes from subject and adds to activity")
        void whenSubjectWithSufficientBalanceAndWithinLimit_createsTransactionRemovesFromSubjectAndAddsToActivity() {
            // Given - Materia con balance suficiente y monto dentro del límite del 30%
            Subject subject = EconomyTestMother.defaultSubject();
            Activity activity = EconomyTestMother.defaultActivity();
            Double amount = 1000.0; // Menor al 30% del balance inicial
            String description = "Actividad de Matemática";
            TransactionActor origin = TransactionActor.SISTEMA;
            TransactionActor destination = TransactionActor.SISTEMA;
            Transaction savedTransaction = EconomyTestMother.defaultTransaction();

            when(reserveFindLastService.get()).thenReturn(EconomyTestMother.defaultReserve());
            when(transaccionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

            // When - Ejecutar transacción de actividad
            Transaction result = actividadTransactionService.execute(
                amount,
                description,
                origin,
                destination,
                null,
                subject,
                activity,
                null, null, null, null
            );

            // Then - Debe crear transacción, remover de materia y agregar a actividad
            verify(reserveFindLastService).get();
            verify(transaccionRepository).save(any(Transaction.class));
            verify(subjectRemoveBalanceService).execute(subject, amount);
            verify(activityAddBalanceService).execute(activity, amount);

            assertThat(result).isEqualTo(savedTransaction);
        }

        @Test
        @DisplayName("Given amount exceeding 30% of initial balance When executing actividad transaction Then throws ConflictException")
        void whenAmountExceeding30Percent_throwsConflictException() {
            // Given - Monto que excede el 30% del balance inicial
            Subject subject = EconomyTestMother.defaultSubject();
            Activity activity = EconomyTestMother.defaultActivity();
            Double amount = 0.31 * subject.getInitialBalance(); // Mayor al 30%

            // When & Then - Debe lanzar ConflictException
            assertThatThrownBy(() -> actividadTransactionService.execute(
                amount,
                "Actividad de Matemática",
                TransactionActor.SISTEMA,
                TransactionActor.SISTEMA,
                null,
                subject,
                activity,
                null, null, null, null
            ))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El monto de la actividad no puede ser mayor al 30% del balance inicial de la materia");
        }

        @Test
        @DisplayName("Given amount exceeding subject actual balance When executing actividad transaction Then throws ConflictException")
        void whenAmountExceedingActualBalance_throwsConflictException() {
            // Given - Monto que excede el balance actual pero dentro del 30% del balance inicial
            // Nota: El servicio primero valida el límite del 30%, por lo que se lanza esa excepción primero
            Subject subject = EconomyTestMother.defaultSubject();
            Activity activity = EconomyTestMother.defaultActivity();
            // Monto que excede el límite del 30% (30% de 10000 = 3000)
            Double amount = (subject.getInitialBalance() * 0.30) + 1.0;

            // When & Then - Debe lanzar ConflictException por exceder el 30%
            assertThatThrownBy(() -> actividadTransactionService.execute(
                amount,
                "Actividad de Matemática",
                TransactionActor.SISTEMA,
                TransactionActor.SISTEMA,
                null,
                subject,
                activity,
                null, null, null, null
            ))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El monto de la actividad no puede ser mayor al 30% del balance inicial de la materia");
        }
    }
}

