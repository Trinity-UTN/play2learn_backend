package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.subject;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.teacher;

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
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class SubjectListTeacherServiceTest {

    @Mock
    private ITeacherGetByEmailService teacherGetByEmailService;

    @Mock
    private ISubjectRepository subjectRepository;

    private SubjectListTeacherService subjectListTeacherService;

    @BeforeEach
    void setUp() {
        subjectListTeacherService = new SubjectListTeacherService(teacherGetByEmailService, subjectRepository);
    }

    @Nested
    @DisplayName("List subjects by teacher")
    class ListSubjects {

        @Test
        @DisplayName("Given existing teacher When listing Then returns mapped subjects and queries repository")
        void listsSubjectsForTeacher() {
            Teacher teacher = teacher(500L);
            User teacherUser = teacherUser(teacher);
            Subject subject = subject(901L, "Fisica Avanzada", course(20L), teacher, List.of());

            when(teacherGetByEmailService.getByEmail(teacherUser.getEmail())).thenReturn(teacher);
            when(subjectRepository.findAllByTeacherAndDeletedAtIsNull(teacher)).thenReturn(List.of(subject));

            List<SubjectResponseDto> result = subjectListTeacherService.cu57ListSubjectsByTeacher(teacherUser);

            verify(subjectRepository).findAllByTeacherAndDeletedAtIsNull(teacher);
            assertThat(result)
                .as("should map repository subjects to DTOs")
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.getTeacher().getId()).isEqualTo(teacher.getId());
                    assertThat(dto.getName()).isEqualTo("Fisica Avanzada");
                });
        }

        @Test
        @DisplayName("Given teacher email not registered When listing Then propagates NotFoundException")
        void teacherNotFoundPropagatesException() {
            User user = User.builder().email("missing@example.com").role(Role.ROLE_TEACHER).build();
            when(teacherGetByEmailService.getByEmail(user.getEmail())).thenThrow(new NotFoundException("teacher"));

            assertThatThrownBy(() -> subjectListTeacherService.cu57ListSubjectsByTeacher(user))
                .isInstanceOf(NotFoundException.class);
        }

        private User teacherUser(Teacher teacher) {
            return teacher.getUser();
        }
    }
}

