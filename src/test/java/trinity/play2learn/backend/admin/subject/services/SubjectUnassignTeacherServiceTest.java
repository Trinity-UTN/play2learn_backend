package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
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
class SubjectUnassignTeacherServiceTest {

    private static final long SUBJECT_ID = 205L;

    @Mock
    private ISubjectRepository subjectRepository;

    @Mock
    private ISubjectGetByIdService getSubjectByIdService;

    private SubjectUnassignTeacherService subjectUnassignTeacherService;

    @BeforeEach
    void setUp() {
        subjectUnassignTeacherService = new SubjectUnassignTeacherService(subjectRepository, getSubjectByIdService);
    }

    @Nested
    @DisplayName("Unassign teacher from subject")
    class UnassignTeacher {

        @Test
        @DisplayName("Given subject with teacher When unassigning Then clears relation and returns DTO without teacher")
        void unassignsTeacherSuccessfully() {
            Subject subject = subjectWithTeacher();
            when(getSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subject);
            when(subjectRepository.save(subject)).thenReturn(subject);

            SubjectResponseDto response = subjectUnassignTeacherService.cu50UnassignTeacher(SUBJECT_ID);

            Subject persistedSubject = captureSavedSubject();
            assertThat(persistedSubject.getTeacher())
                .as("teacher reference should be cleared")
                .isNull();
            assertThat(response.getTeacher()).isNull();
        }

        @Test
        @DisplayName("Given subject already without teacher When unassigning Then keeps state and persists once")
        void unassigningSubjectWithoutTeacher_isIdempotent() {
            Subject subject = subjectWithoutTeacher();

            when(getSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subject);
            when(subjectRepository.save(subject)).thenReturn(subject);

            subjectUnassignTeacherService.cu50UnassignTeacher(SUBJECT_ID);

            Subject persistedSubject = captureSavedSubject();
            assertThat(persistedSubject.getTeacher())
                .as("subject should remain without teacher")
                .isNull();
        }

        private Subject subjectWithTeacher() {
            return subject(
                SUBJECT_ID,
                "Biologia",
                course(13L),
                teacher(400L),
                new ArrayList<>()
            );
        }

        private Subject subjectWithoutTeacher() {
            return subject(
                SUBJECT_ID,
                "Geografia",
                course(12L),
                null,
                new ArrayList<>()
            );
        }

        private Subject captureSavedSubject() {
            ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
            verify(subjectRepository).save(subjectCaptor.capture());
            return subjectCaptor.getValue();
        }
    }
}

