package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.students;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.subject;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.teacher;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

@ExtendWith(MockitoExtension.class)
class SubjectRefillBalanceServiceTest {

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    private SubjectRefillBalanceService subjectRefillBalanceService;

    @BeforeEach
    void setUp() {
        subjectRefillBalanceService = new SubjectRefillBalanceService(subjectRepository, transactionGenerateService);
    }

    @Nested
    @DisplayName("Refill balance execution")
    class RefillBalance {

        @Test
        @DisplayName("Given subjects with missing balance When refilling Then generates transactions and returns mapped DTOs")
        void refillGeneratesTransactionsAndReturnsDtos() {
            Subject subjectNeedingRefill = subjectWithBalance(10L, 1000.0, 0.0, students(1L, 2L));
            Subject subjectUpToDate = subjectWithBalance(11L, 5000.0, 5000.0, students(3L));
            List<Subject> iterableSubjects = List.of(subjectNeedingRefill, subjectUpToDate);

            when(subjectRepository.findAllByDeletedAtIsNull())
                .thenReturn(iterableSubjects)
                .thenReturn(iterableSubjects);

            List<SubjectResponseDto> result = subjectRefillBalanceService.cu58RefillBalance();

            verify(transactionGenerateService).generate(
                TypeTransaction.ASIGNACION,
                9000.0,
                "Asignación de monedas mensual.",
                TransactionActor.SISTEMA,
                TransactionActor.SISTEMA,
                null,
                subjectNeedingRefill,
                null,
                null,
                null,
                null,
                null
            );
            verifyNoMoreInteractions(transactionGenerateService);

            assertThat(subjectNeedingRefill.getInitialBalance())
                .as("initial balance should be recalculated based on student count")
                .isEqualTo(10000.0);
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Given subjects already refilled When executing Then skips transaction generation and returns mapped list")
        void refillSkipsWhenNoBalanceNeeded() {
            Subject subject = subjectWithBalance(12L, 5000.0, 5000.0, students(4L));
            when(subjectRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(subject));

            List<SubjectResponseDto> result = subjectRefillBalanceService.cu58RefillBalance();

            verifyNoInteractions(transactionGenerateService);
            assertThat(result).hasSize(1);
        }

        private Subject subjectWithBalance(Long id, double actualBalance, double initialBalance, List<trinity.play2learn.backend.admin.student.models.Student> studentList) {
            Subject subject = subject(
                id,
                "Materia " + id,
                course(id),
                teacher(id),
                new ArrayList<>(studentList)
            );
            subject.setActualBalance(actualBalance);
            subject.setInitialBalance(initialBalance);
            return subject;
        }
    }
}

