package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.students;
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
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@ExtendWith(MockitoExtension.class)
class SubjectDeleteServiceTest {

    private static final long SUBJECT_ID = 77L;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ISubjectGetByIdService findSubjectByIdService;

    private SubjectDeleteService subjectDeleteService;

    @BeforeEach
    void setUp() {
        subjectDeleteService = new SubjectDeleteService(subjectRepository, findSubjectByIdService);
    }

    @Nested
    @DisplayName("Delete subject flow")
    class DeleteSubject {

        @Test
        @DisplayName("Given subject without students When deleting Then marks deletedAt and persists changes")
        void withoutStudents_marksDeleted() {
            Subject subject = activeSubjectWithoutStudents();
            when(findSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subject);

            subjectDeleteService.cu30DeleteSubject(SUBJECT_ID);

            ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
            verify(subjectRepository).save(subjectCaptor.capture());

            Subject persistedSubject = subjectCaptor.getValue();
            assertThat(persistedSubject.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("Given subject with assigned students When deleting Then throws ConflictException and skips persistence")
        void withStudents_throwsConflict() {
            Subject subject = activeSubjectWithStudents();
            when(findSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subject);

            assertThatThrownBy(() -> subjectDeleteService.cu30DeleteSubject(SUBJECT_ID))
                .isInstanceOf(ConflictException.class);

            verify(subjectRepository, never()).save(any());
        }
    }

    private Subject activeSubjectWithoutStudents() {
        return subject(
            SUBJECT_ID,
            "Matematica",
            course(1L),
            teacher(10L),
            new ArrayList<>()
        );
    }

    private Subject activeSubjectWithStudents() {
        return subject(
            SUBJECT_ID,
            "Historia",
            course(1L),
            teacher(10L),
            new ArrayList<>(students(1L))
        );
    }
}
