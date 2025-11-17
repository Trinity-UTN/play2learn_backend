package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import trinity.play2learn.backend.admin.subject.dtos.SubjectAssignTeacherRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SubjectAssignTeacherServiceTest {

    private static final long SUBJECT_ID = 200L;
    private static final long TEACHER_ID = 300L;

    @Mock
    private ISubjectRepository subjectRepository;
    @Mock
    private ISubjectGetByIdService getSubjectByIdService;
    @Mock
    private ITeacherGetByIdService getTeacherByIdService;

    private SubjectAssignTeacherService subjectAssignTeacherService;

    @BeforeEach
    void setUp() {
        subjectAssignTeacherService = new SubjectAssignTeacherService(
            subjectRepository,
            getSubjectByIdService,
            getTeacherByIdService
        );
    }

    @Nested
    @DisplayName("Assign teacher to subject")
    class AssignTeacher {

        @Test
        @DisplayName("Given valid subject and teacher When assigning Then persists relation and returns DTO")
        void assignsTeacherSuccessfully() {
            Subject subject = subjectWithoutTeacher();
            Teacher teacher = teacher(TEACHER_ID);

            when(getSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subject);
            when(getTeacherByIdService.findById(TEACHER_ID)).thenReturn(teacher);
            when(subjectRepository.save(subject)).thenReturn(subject);

            SubjectResponseDto response = subjectAssignTeacherService.cu49AssignTeacher(requestDto());

            Subject persistedSubject = captureSavedSubject();
            assertThat(persistedSubject.getTeacher())
                .as("subject should reference the assigned teacher")
                .isEqualTo(teacher);
            assertThat(response.getTeacher().getId()).isEqualTo(TEACHER_ID);
        }

        @Test
        @DisplayName("Given request referencing non existing teacher When assigning Then propagates NotFoundException")
        void teacherNotFoundPropagatesException() {
            when(getSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subjectWithoutTeacher());
            when(getTeacherByIdService.findById(TEACHER_ID)).thenThrow(new NotFoundException("teacher not found"));

            assertThatThrownBy(() -> subjectAssignTeacherService.cu49AssignTeacher(requestDto()))
                .isInstanceOf(NotFoundException.class);
        }

        private SubjectAssignTeacherRequestDto requestDto() {
            return SubjectAssignTeacherRequestDto.builder()
                .subjectId(SUBJECT_ID)
                .teacherId(TEACHER_ID)
                .build();
        }

        private Subject subjectWithoutTeacher() {
            return subject(
                SUBJECT_ID,
                "Literatura",
                course(10L),
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

