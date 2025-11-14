package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.subject;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.teacher;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;

@ExtendWith(MockitoExtension.class)
class SubjectAddBalanceServiceTest {

    @Mock
    private ISubjectRepository subjectRepository;

    private SubjectAddBalanceService subjectAddBalanceService;

    @BeforeEach
    void setUp() {
        subjectAddBalanceService = new SubjectAddBalanceService(subjectRepository);
    }

    @Nested
    @DisplayName("Add balance execution")
    class Execute {

        @Test
        @DisplayName("Given positive amount When executing Then increases balance and saves subject")
        void execute_addsBalanceAndPersists() {
            Subject subject = subjectReadyForBalance(1000.0, 1000.0);

            when(subjectRepository.save(subject)).thenReturn(subject);

            Subject result = subjectAddBalanceService.execute(subject, 500.0);

            Subject savedSubject = captureSavedSubject();
            assertThat(savedSubject.getActualBalance())
                .as("actual balance should increase by the amount")
                .isEqualTo(1500.0);
            assertThat(savedSubject.getInitialBalance())
                .as("initial balance should mirror actual balance after addition")
                .isEqualTo(1500.0);
            assertThat(result).isSameAs(savedSubject);
        }

        @Test
        @DisplayName("Given zero or negative amount When executing Then throws IllegalArgumentException and skips save")
        void execute_withNonPositiveAmount_throwsException() {
            Subject subject = subjectReadyForBalance(2000.0, 2000.0);

            assertThatThrownBy(() -> subjectAddBalanceService.execute(subject, 0.0))
                .isInstanceOf(IllegalArgumentException.class);

            verify(subjectRepository, never()).save(subject);
        }

        private Subject subjectReadyForBalance(double actual, double initial) {
            Subject subject = subject(1L, "Quimica", course(1L), teacher(1L), new ArrayList<>());
            subject.setActualBalance(actual);
            subject.setInitialBalance(initial);
            return subject;
        }

        private Subject captureSavedSubject() {
            ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
            verify(subjectRepository).save(subjectCaptor.capture());
            return subjectCaptor.getValue();
        }
    }
}

