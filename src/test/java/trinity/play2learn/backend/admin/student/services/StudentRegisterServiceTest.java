package trinity.play2learn.backend.admin.student.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.commons.CourseGetByIdService;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddStudentByCourseService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.user.interfaces.IUserCreateService;

@ExtendWith(MockitoExtension.class)
class StudentRegisterServiceTest {

    private static final Long COURSE_ID = 101L;
    private static final String EMAIL = "student@example.com";
    private static final String DNI = "12345678";

    @Mock
    private CourseGetByIdService courseGetByIdService;
    @Mock
    private IUserCreateService userCreateService;
    @Mock
    private IStudentRepository studentRepository;
    @Mock
    private ISubjectAddStudentByCourseService subjectAddStudentByCourseService;

    private StudentRegisterService studentRegisterService;

    @BeforeEach
    void setUp() {
        studentRegisterService = new StudentRegisterService(
            courseGetByIdService,
            userCreateService,
            studentRepository,
            subjectAddStudentByCourseService
        );
    }

    @Nested
    @DisplayName("cu4registerStudent")
    class RegisterStudent {

        @Test
        @DisplayName("Given valid student request When registering Then creates user, persists student with profile/wallet and assigns subjects")
        void whenRequestValid_persistsStudentAndAssignsSubjects() {
            StudentRequestDto request = StudentTestMother.registerRequestBuilder(COURSE_ID)
                .email(EMAIL)
                .dni(DNI)
                .build();

            Course course = StudentTestMother.course(COURSE_ID);
            User createdUser = StudentTestMother.studentUser(300L, EMAIL);

            when(courseGetByIdService.findById(COURSE_ID)).thenReturn(course);
            when(userCreateService.create(EMAIL, DNI, Role.ROLE_STUDENT)).thenReturn(createdUser);
            when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
                Student student = invocation.getArgument(0, Student.class);
                student.setId(501L);
                return student;
            });

            StudentResponseDto responseDto = studentRegisterService.cu4registerStudent(request);

            verify(courseGetByIdService).findById(COURSE_ID);
            verify(userCreateService).create(EMAIL, DNI, Role.ROLE_STUDENT);

            ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(studentCaptor.capture());
            Student savedStudent = studentCaptor.getValue();

            verify(subjectAddStudentByCourseService).addStudentByCourse(course, savedStudent);

            assertThat(savedStudent.getUser()).isEqualTo(createdUser);
            assertThat(savedStudent.getProfile()).isNotNull();
            assertThat(savedStudent.getProfile().getStudent()).isEqualTo(savedStudent);
            assertThat(savedStudent.getWallet()).isNotNull();
            assertThat(savedStudent.getWallet().getBalance()).isZero();
            assertThat(savedStudent.getWallet().getInvertedBalance()).isZero();

            assertThat(responseDto.getId()).isEqualTo(501L);
            assertThat(responseDto.getName()).isEqualTo(request.getName());
            assertThat(responseDto.getUser().getEmail()).isEqualTo(EMAIL);
            assertThat(responseDto.getCourse().getId()).isEqualTo(COURSE_ID);
            assertThat(responseDto.getWallet().getBalance()).isZero();
            assertThat(responseDto.getProfile().getOwnedAspects()).isEmpty();
        }

        @Test
        @DisplayName("Given course is missing When registering student Then propagates NotFoundException and stops flow")
        void whenCourseMissing_propagatesNotFound() {
            StudentRequestDto request = StudentTestMother.registerRequestBuilder(COURSE_ID)
                .email(EMAIL)
                .dni(DNI)
                .build();

            when(courseGetByIdService.findById(COURSE_ID)).thenThrow(new NotFoundException("course not found"));

            assertThatThrownBy(() -> studentRegisterService.cu4registerStudent(request))
                .isInstanceOf(NotFoundException.class);

            verify(courseGetByIdService).findById(COURSE_ID);
            verifyNoInteractions(userCreateService, studentRepository, subjectAddStudentByCourseService);
        }

        @Test
        @DisplayName("Given duplicate user detected When registering student Then throws ConflictException and avoids persistence")
        void whenUserAlreadyExists_throwsConflict() {
            StudentRequestDto request = StudentTestMother.registerRequestBuilder(COURSE_ID)
                .email(EMAIL)
                .dni(DNI)
                .build();

            Course course = StudentTestMother.course(COURSE_ID);

            when(courseGetByIdService.findById(COURSE_ID)).thenReturn(course);
            when(userCreateService.create(EMAIL, DNI, Role.ROLE_STUDENT))
                .thenThrow(new ConflictException("User already exists"));

            assertThatThrownBy(() -> studentRegisterService.cu4registerStudent(request))
                .isInstanceOf(ConflictException.class);

            verify(courseGetByIdService).findById(COURSE_ID);
            verify(userCreateService).create(EMAIL, DNI, Role.ROLE_STUDENT);
            verify(subjectAddStudentByCourseService, never()).addStudentByCourse(any(), any());
            verify(studentRepository, never()).save(any());
        }
    }
}

