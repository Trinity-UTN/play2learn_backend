package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;

@ExtendWith(MockitoExtension.class)
class SubjectRestoreServiceTest {

    private static final long SUBJECT_ID = 88L;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ISubjectGetByIdService findSubjectByIdService;

    private SubjectRestoreService subjectRestoreService;

    @BeforeEach
    void setUp() {
        subjectRestoreService = new SubjectRestoreService(subjectRepository, findSubjectByIdService);
    }

    @Nested
    @DisplayName("Restore subject flow")
    class RestoreSubject {

        @Test
        @DisplayName("Given soft deleted subject When restoring Then clears deletedAt and returns mapped DTO")
        void restoresSoftDeletedSubject() {
            Subject deletedSubject = deletedSubject();
            when(findSubjectByIdService.findDeletedById(SUBJECT_ID)).thenReturn(deletedSubject);
            when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

            SubjectResponseDto response = subjectRestoreService.cu34RestoreSubject(SUBJECT_ID);

            ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
            verify(subjectRepository).save(subjectCaptor.capture());

            Subject restoredSubject = subjectCaptor.getValue();
            assertThat(restoredSubject.getDeletedAt()).isNull();

            assertThat(response.getId()).isEqualTo(SUBJECT_ID);
            assertThat(response.getName()).isEqualTo("Quimica");
            assertThat(response.getCourse().getId()).isEqualTo(deletedSubject.getCourse().getId());
            assertThat(response.getTeacher().getId()).isEqualTo(deletedSubject.getTeacher().getId());
        }
    }

    private Subject deletedSubject() {
        Subject subject = subject(
            SUBJECT_ID,
            "Quimica",
            course(2L),
            teacher(20L),
            new ArrayList<>()
        );
        subject.delete();
        return subject;
    }
}

