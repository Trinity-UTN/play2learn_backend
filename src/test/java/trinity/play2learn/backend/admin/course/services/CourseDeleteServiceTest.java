package trinity.play2learn.backend.admin.course.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsExistByCourseService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectsExistsByCourseService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseDeleteService")
class CourseDeleteServiceTest {

    private static final Long COURSE_ID = 7L;
    private static final Long YEAR_ID = 2025L;
    private static final String COURSE_NAME = "Matemática I";
    private static final String YEAR_NAME = "2025";

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private ICourseGetByIdService courseGetByIdService;

    @Mock
    private IStudentsExistByCourseService studentsExistByCourseService;

    @Mock
    private ISubjectsExistsByCourseService subjectsExistsByCourseService;

    @InjectMocks
    private CourseDeleteService courseDeleteService;

    @Nested
    @DisplayName("cu15DeleteCourse")
    class Cu15DeleteCourse {

        private Course existingCourse;

        private Course arrangeCourseFound() {
            existingCourse = buildCourse();
            when(courseGetByIdService.findById(COURSE_ID)).thenReturn(existingCourse);
            return existingCourse;
        }

        @Test
        @DisplayName("Debe eliminar lógicamente el curso cuando no existen asociaciones")
        void shouldSoftDeleteCourseWhenNoAssociations() {
            // Given
            Course course = arrangeCourseFound();

            // When
            courseDeleteService.cu15DeleteCourse(COURSE_ID);

            // Then
            verify(studentsExistByCourseService).validate(COURSE_ID);
            verify(subjectsExistsByCourseService).validate(course);

            ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
            verify(courseRepository).save(courseCaptor.capture());

            Course savedCourse = courseCaptor.getValue();
            assertThat(savedCourse.getId()).isEqualTo(COURSE_ID);
            assertThat(savedCourse.getDeletedAt())
                .isNotNull()
                .isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Debe lanzar ConflictException cuando existen estudiantes asociados")
        void shouldThrowConflictWhenStudentsExist() {
            // Given
            Course course = arrangeCourseFound();

            ConflictException conflict = new ConflictException("Estudiante asociado al curso");
            doThrow(conflict).when(studentsExistByCourseService).validate(COURSE_ID);

            // When + Then
            assertThatThrownBy(() -> courseDeleteService.cu15DeleteCourse(COURSE_ID))
                .isSameAs(conflict);

            verify(subjectsExistsByCourseService, never()).validate(course);
            verify(courseRepository, never()).save(course);
        }

        @Test
        @DisplayName("Debe lanzar ConflictException cuando existen materias asociadas")
        void shouldThrowConflictWhenSubjectsExist() {
            // Given
            Course course = arrangeCourseFound();

            ConflictException conflict = new ConflictException("Materia asociada al curso");
            doThrow(conflict).when(subjectsExistsByCourseService).validate(course);

            // When + Then
            assertThatThrownBy(() -> courseDeleteService.cu15DeleteCourse(COURSE_ID))
                .isSameAs(conflict);

            verify(courseRepository, never()).save(course);
        }

        @Test
        @DisplayName("Debe propagar NotFoundException cuando el curso no existe o está eliminado")
        void shouldPropagateNotFoundWhenCourseDoesNotExist() {
            // Given
            NotFoundException notFound = new NotFoundException("Curso no encontrado");
            when(courseGetByIdService.findById(COURSE_ID)).thenThrow(notFound);

            // When + Then
            assertThatThrownBy(() -> courseDeleteService.cu15DeleteCourse(COURSE_ID))
                .isSameAs(notFound);
        }
    }

    private Course buildCourse() {
        return Course.builder()
            .id(COURSE_ID)
            .name(COURSE_NAME)
            .year(Year.builder()
                .id(YEAR_ID)
                .name(YEAR_NAME)
                .build())
            .build();
    }
}

