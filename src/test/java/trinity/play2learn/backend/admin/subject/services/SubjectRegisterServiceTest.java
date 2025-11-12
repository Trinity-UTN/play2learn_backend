package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.students;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.teacher;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByCourseService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectExistsByNameAndCourseService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SubjectRegisterServiceTest {

    private static final long COURSE_ID = 101L;
    private static final long TEACHER_ID = 501L;

    @Mock
    private ICourseGetByIdService courseGetByIdService;
    @Mock
    private ITeacherGetByIdService getTeacherByIdService;
    @Mock
    private IStudentGetByCourseService courseGetStudentsService;
    @Mock
    private ISubjectRepository subjectRepository;
    @Mock
    private ISubjectExistsByNameAndCourseService validateSubjectService;

    private SubjectRegisterService subjectRegisterService;

    @BeforeEach
    void setUp() {
        subjectRegisterService = new SubjectRegisterService(
            courseGetByIdService,
            getTeacherByIdService,
            courseGetStudentsService,
            subjectRepository,
            validateSubjectService
        );
    }

    @Test
    @DisplayName("Given mandatory subject request with teacher When registering Then assigns course students and initializes balances")
    void registerSubject_whenMandatory_assignsCourseStudents() {
        SubjectRequestDto request = SubjectRequestDto.builder()
            .name("Programación I")
            .courseId(COURSE_ID)
            .teacherId(TEACHER_ID)
            .optional(false)
            .build();

        Course course = course(COURSE_ID);
        var teacher = teacher(TEACHER_ID);
        List<Student> expectedStudents = students(1L, 2L);

        when(courseGetByIdService.findById(COURSE_ID)).thenReturn(course);
        when(getTeacherByIdService.findById(TEACHER_ID)).thenReturn(teacher);
        when(courseGetStudentsService.getStudentsByCourseId(COURSE_ID)).thenReturn(new ArrayList<>(expectedStudents));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject subjectToPersist = invocation.getArgument(0, Subject.class);
            subjectToPersist.setId(900L);
            return subjectToPersist;
        });

        SubjectResponseDto response = subjectRegisterService.cu28RegisterSubject(request);

        verify(validateSubjectService).existByNameAndCourse(request.getName(), course);
        verify(courseGetByIdService).findById(COURSE_ID);
        verify(getTeacherByIdService).findById(TEACHER_ID);
        verify(courseGetStudentsService).getStudentsByCourseId(COURSE_ID);

        ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(subjectCaptor.capture());

        Subject savedSubject = subjectCaptor.getValue();
        assertThat(savedSubject.getName()).isEqualTo(request.getName());
        assertThat(savedSubject.getCourse()).isEqualTo(course);
        assertThat(savedSubject.getTeacher()).isEqualTo(teacher);
        assertThat(savedSubject.getStudents()).containsExactlyElementsOf(expectedStudents);
        assertThat(savedSubject.getActualBalance()).isZero();
        assertThat(savedSubject.getInitialBalance()).isZero();

        assertThat(response.getId()).isEqualTo(900L);
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getCourse().getId()).isEqualTo(course.getId());
        assertThat(response.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(response.getStudents()).hasSize(expectedStudents.size());
    }

    @Test
    @DisplayName("Given duplicated subject name in course When registering Then throws ConflictException and aborts persistence")
    void registerSubject_whenNameAlreadyExists_throwsConflictException() {
        SubjectRequestDto request = SubjectRequestDto.builder()
            .name("Historia")
            .courseId(COURSE_ID)
            .teacherId(null)
            .optional(true)
            .build();

        Course course = course(COURSE_ID);

        when(courseGetByIdService.findById(COURSE_ID)).thenReturn(course);

        doThrowConflictOnValidate(request.getName(), course);

        assertThatThrownBy(() -> subjectRegisterService.cu28RegisterSubject(request))
            .isInstanceOf(ConflictException.class);

        verify(courseGetByIdService).findById(COURSE_ID);
        verify(validateSubjectService).existByNameAndCourse(request.getName(), course);
        verifyNoInteractions(getTeacherByIdService);
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given subject request referencing missing teacher When registering Then propagates NotFoundException and stops flow")
    void registerSubject_whenTeacherNotFound_throwsNotFoundException() {
        SubjectRequestDto request = SubjectRequestDto.builder()
            .name("Arte")
            .courseId(COURSE_ID)
            .teacherId(TEACHER_ID)
            .optional(true)
            .build();

        Course course = course(COURSE_ID);

        when(courseGetByIdService.findById(COURSE_ID)).thenReturn(course);
        when(getTeacherByIdService.findById(TEACHER_ID)).thenThrow(new NotFoundException("teacher not found"));

        assertThatThrownBy(() -> subjectRegisterService.cu28RegisterSubject(request))
            .isInstanceOf(NotFoundException.class);

        verify(validateSubjectService).existByNameAndCourse(request.getName(), course);
        verify(courseGetStudentsService, never()).getStudentsByCourseId(any());
        verify(subjectRepository, never()).save(any());
    }

    private void doThrowConflictOnValidate(String name, Course course) {
        doThrow(new ConflictException("exists"))
            .when(validateSubjectService)
            .existByNameAndCourse(name, course);
    }
}