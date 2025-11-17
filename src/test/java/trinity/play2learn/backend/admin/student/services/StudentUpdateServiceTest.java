package trinity.play2learn.backend.admin.student.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.dtos.StudentUpdateRequestDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentExistByDNIService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class StudentUpdateServiceTest {

    private static final long STUDENT_ID = 900L;
    private static final long ORIGINAL_COURSE_ID = 10L;
    private static final String ORIGINAL_DNI = "12345678";

    @Mock
    private IStudentGetByIdService studentGetByIdService;
    @Mock
    private IStudentExistByDNIService studentExistByDNIService;
    @Mock
    private ICourseGetByIdService courseGetByIdService;
    @Mock
    private IStudentRepository studentRepository;

    private StudentUpdateService studentUpdateService;

    @BeforeEach
    void setUp() {
        studentUpdateService = new StudentUpdateService(
            studentGetByIdService,
            studentExistByDNIService,
            courseGetByIdService,
            studentRepository
        );
    }

    @Nested
    @DisplayName("cu18updateStudent")
    class UpdateStudent {

        @Test
        @DisplayName("Given valid update request with new course When updating Then persists student with updated data")
        void whenDataValid_updatesStudent() {
            Student existing = StudentTestMother.student(STUDENT_ID, ORIGINAL_DNI, ORIGINAL_COURSE_ID);

            StudentUpdateRequestDto request = StudentTestMother.updateRequestBuilder(20L)
                .dni("87654321")
                .build();

            Course newCourse = StudentTestMother.course(20L, "4to B");

            when(studentGetByIdService.findById(STUDENT_ID)).thenReturn(existing);
            when(studentExistByDNIService.validate(request.getDni())).thenReturn(false);
            when(courseGetByIdService.findById(20L)).thenReturn(newCourse);
            when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0, Student.class));

            StudentResponseDto response = studentUpdateService.cu18updateStudent(request, STUDENT_ID);

            verify(studentGetByIdService).findById(STUDENT_ID);
            verify(studentExistByDNIService).validate(request.getDni());
            verify(courseGetByIdService).findById(20L);

            ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(studentCaptor.capture());
            Student savedStudent = studentCaptor.getValue();

            assertThat(savedStudent.getId()).isEqualTo(STUDENT_ID);
            assertThat(savedStudent.getName()).isEqualTo(request.getName());
            assertThat(savedStudent.getLastname()).isEqualTo(request.getLastname());
            assertThat(savedStudent.getDni()).isEqualTo(request.getDni());
            assertThat(savedStudent.getCourse()).isEqualTo(newCourse);
            assertThat(savedStudent.getUser()).isEqualTo(existing.getUser());

            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getLastname()).isEqualTo(request.getLastname());
            assertThat(response.getDni()).isEqualTo(request.getDni());
            assertThat(response.getCourse().getId()).isEqualTo(newCourse.getId());
        }

        @Test
        @DisplayName("Given duplicated DNI in update request When updating Then throws ConflictException and avoids persistence")
        void whenDniAlreadyExists_throwsConflict() {
            Student existing = StudentTestMother.student(STUDENT_ID, ORIGINAL_DNI, ORIGINAL_COURSE_ID);

            StudentUpdateRequestDto request = StudentTestMother.updateRequestBuilder(ORIGINAL_COURSE_ID)
                .dni("87654321")
                .build();

            when(studentGetByIdService.findById(STUDENT_ID)).thenReturn(existing);
            when(studentExistByDNIService.validate(request.getDni())).thenReturn(true);

            assertThatThrownBy(() -> studentUpdateService.cu18updateStudent(request, STUDENT_ID))
                .isInstanceOf(ConflictException.class);

            verify(studentExistByDNIService).validate(request.getDni());
            verify(studentRepository, never()).save(any());
            verify(courseGetByIdService, never()).findById(any());
        }

        @Test
        @DisplayName("Given student not found When updating Then propagates NotFoundException")
        void whenStudentMissing_propagatesNotFound() {
            StudentUpdateRequestDto request = StudentTestMother.updateRequestBuilder(ORIGINAL_COURSE_ID)
                .dni(ORIGINAL_DNI)
                .build();

            when(studentGetByIdService.findById(STUDENT_ID)).thenThrow(new NotFoundException("student not found"));

            assertThatThrownBy(() -> studentUpdateService.cu18updateStudent(request, STUDENT_ID))
                .isInstanceOf(NotFoundException.class);

            verify(studentGetByIdService).findById(STUDENT_ID);
            verify(studentExistByDNIService, never()).validate(any());
            verify(courseGetByIdService, never()).findById(any());
            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given same DNI and course in update request When updating Then reuses existing course and skips validations")
        void whenDniAndCourseUnchanged_reusesExistingCourse() {
            Student existing = StudentTestMother.student(STUDENT_ID, ORIGINAL_DNI, ORIGINAL_COURSE_ID);

            StudentUpdateRequestDto request = StudentTestMother.updateRequestBuilder(ORIGINAL_COURSE_ID)
                .dni(ORIGINAL_DNI)
                .name("Ana Actualizada")
                .build();

            when(studentGetByIdService.findById(STUDENT_ID)).thenReturn(existing);
            when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0, Student.class));

            StudentResponseDto response = studentUpdateService.cu18updateStudent(request, STUDENT_ID);

            verify(studentExistByDNIService, never()).validate(any());
            verify(courseGetByIdService, never()).findById(any());

            ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(studentCaptor.capture());
            Student savedStudent = studentCaptor.getValue();

            assertThat(savedStudent.getCourse()).isEqualTo(existing.getCourse());
            assertThat(savedStudent.getDni()).isEqualTo(ORIGINAL_DNI);
            assertThat(response.getCourse().getId()).isEqualTo(existing.getCourse().getId());
        }
    }
}

