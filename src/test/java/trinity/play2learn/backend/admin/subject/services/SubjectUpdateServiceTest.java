package trinity.play2learn.backend.admin.subject.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.course;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.students;
import static trinity.play2learn.backend.admin.subject.services.SubjectTestMother.subject;
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
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectUpdateRequestDto;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectExistsByNameAndCourseService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SubjectUpdateServiceTest {

    private static final long SUBJECT_ID = 301L;
    private static final long COURSE_ID = 101L;
    private static final long TEACHER_ID = 501L;

    @Mock
    private ICourseGetByIdService courseGetByIdService;
    @Mock
    private ITeacherGetByIdService getTeacherByIdService;
    @Mock
    private ISubjectGetByIdService findSubjectByIdService;
    @Mock
    private ISubjectRepository subjectRepository;
    @Mock
    private ISubjectExistsByNameAndCourseService validateSubjectService;

    private SubjectUpdateService subjectUpdateService;

    @BeforeEach
    void setUp() {
        subjectUpdateService = new SubjectUpdateService(
            courseGetByIdService,
            getTeacherByIdService,
            findSubjectByIdService,
            subjectRepository,
            validateSubjectService
        );
    }

    @Test
    @DisplayName("Given existing subject with assigned students When updating Then preserves students and applies new metadata")
    void updateSubject_successfulUpdate_preservesStudents() {
        SubjectUpdateRequestDto request = SubjectUpdateRequestDto.builder()
            .name("Programación II")
            .courseId(COURSE_ID)
            .teacherId(TEACHER_ID)
            .optional(true)
            .build();

        Course newCourse = course(COURSE_ID);
        var teacher = teacher(TEACHER_ID);
        Subject subjectInDb = subject(
            SUBJECT_ID,
            "Original",
            course(COURSE_ID),
            teacher(TEACHER_ID),
            new ArrayList<>(students(10L))
        );
        List<Student> existingStudents = new ArrayList<>(subjectInDb.getStudents());

        when(findSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subjectInDb);
        when(courseGetByIdService.findById(COURSE_ID)).thenReturn(newCourse);
        when(getTeacherByIdService.findById(TEACHER_ID)).thenReturn(teacher);
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject subject = invocation.getArgument(0, Subject.class);
            subject.setId(SUBJECT_ID);
            return subject;
        });

        SubjectResponseDto response = subjectUpdateService.cu29UpdateSubject(SUBJECT_ID, request);

        verify(validateSubjectService).existByNameAndCourseAndIdNot(request.getName(), newCourse, SUBJECT_ID);

        ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(subjectCaptor.capture());

        Subject updatedSubject = subjectCaptor.getValue();
        assertThat(updatedSubject.getId()).isEqualTo(SUBJECT_ID);
        assertThat(updatedSubject.getName()).isEqualTo(request.getName());
        assertThat(updatedSubject.getCourse()).isEqualTo(newCourse);
        assertThat(updatedSubject.getTeacher()).isEqualTo(teacher);
        assertThat(updatedSubject.getStudents()).containsExactlyElementsOf(existingStudents);

        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getCourse().getId()).isEqualTo(newCourse.getId());
        assertThat(response.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(response.getStudents()).hasSize(existingStudents.size());
    }

    @Test
    @DisplayName("Given duplicated subject name for same course When updating Then throws ConflictException and avoids persistence")
    void updateSubject_whenNameAlreadyExists_throwsConflictException() {
        SubjectUpdateRequestDto request = SubjectUpdateRequestDto.builder()
            .name("Historia")
            .courseId(COURSE_ID)
            .teacherId(null)
            .optional(false)
            .build();

        Course newCourse = course(COURSE_ID);
        Subject subjectInDb = subject(
            SUBJECT_ID,
            "Original",
            course(COURSE_ID),
            teacher(TEACHER_ID),
            new ArrayList<>()
        );

        when(findSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subjectInDb);
        when(courseGetByIdService.findById(COURSE_ID)).thenReturn(newCourse);
        doThrow(new ConflictException("exists"))
            .when(validateSubjectService)
            .existByNameAndCourseAndIdNot(request.getName(), newCourse, SUBJECT_ID);

        assertThatThrownBy(() -> subjectUpdateService.cu29UpdateSubject(SUBJECT_ID, request))
            .isInstanceOf(ConflictException.class);

        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given update request referencing missing teacher When updating Then throws NotFoundException without saving")
    void updateSubject_whenTeacherNotFound_throwsNotFoundException() {
        SubjectUpdateRequestDto request = SubjectUpdateRequestDto.builder()
            .name("Arte")
            .courseId(COURSE_ID)
            .teacherId(TEACHER_ID)
            .optional(true)
            .build();

        Subject subjectInDb = subject(
            SUBJECT_ID,
            "Original",
            course(COURSE_ID),
            teacher(TEACHER_ID),
            new ArrayList<>()
        );
        Course course = course(COURSE_ID);

        when(findSubjectByIdService.findById(SUBJECT_ID)).thenReturn(subjectInDb);
        when(courseGetByIdService.findById(COURSE_ID)).thenReturn(course);
        when(getTeacherByIdService.findById(TEACHER_ID)).thenThrow(new NotFoundException("teacher not found"));

        assertThatThrownBy(() -> subjectUpdateService.cu29UpdateSubject(SUBJECT_ID, request))
            .isInstanceOf(NotFoundException.class);

        verify(validateSubjectService).existByNameAndCourseAndIdNot(request.getName(), course, SUBJECT_ID);
        verify(subjectRepository, never()).save(any());
    }
}