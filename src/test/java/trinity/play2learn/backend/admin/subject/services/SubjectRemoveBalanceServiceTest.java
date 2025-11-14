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
class SubjectRemoveBalanceServiceTest {

    @Mock
    private ISubjectRepository subjectRepository;

    private SubjectRemoveBalanceService subjectRemoveBalanceService;

    @BeforeEach
    void setUp() {
        subjectRemoveBalanceService = new SubjectRemoveBalanceService(subjectRepository);
    }

    @Nested
    @DisplayName("Remove balance execution")
    class Execute {

        @Test
        @DisplayName("Given positive amount When executing Then subtracts balance and saves subject")
        void execute_removesBalanceAndPersists() {
            Subject subject = subjectWithBalance(800.0);

            when(subjectRepository.save(subject)).thenReturn(subject);

            Subject result = subjectRemoveBalanceService.execute(subject, 200.0);

            Subject savedSubject = captureSavedSubject();
            assertThat(savedSubject.getActualBalance())
                .as("actual balance should decrease by the amount")
                .isEqualTo(600.0);
            assertThat(result).isSameAs(savedSubject);
        }

        @Test
        @DisplayName("Given zero or negative amount When executing Then throws IllegalArgumentException and skips save")
        void execute_withNonPositiveAmount_throwsException() {
            Subject subject = subjectWithBalance(600.0);

            assertThatThrownBy(() -> subjectRemoveBalanceService.execute(subject, -10.0))
                .isInstanceOf(IllegalArgumentException.class);

            verify(subjectRepository, never()).save(subject);
        }

        private Subject subjectWithBalance(double balance) {
            Subject subject = subject(3L, "Historia", course(3L), teacher(3L), new ArrayList<>());
            subject.setActualBalance(balance);
            return subject;
        }

        private Subject captureSavedSubject() {
            ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
            verify(subjectRepository).save(subjectCaptor.capture());
            return subjectCaptor.getValue();
        }
    }
}

