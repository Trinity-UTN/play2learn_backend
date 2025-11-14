package trinity.play2learn.backend.admin.course.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.dtos.CourseUpdateDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByService;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseUpdateService")
class CourseUpdateServiceTest {

    private static final Long COURSE_ID = 5L;
    private static final Long YEAR_ID = 2025L;
    private static final String YEAR_NAME = "2025";
    private static final String ORIGINAL_NAME = "Matemática I";
    private static final String UPDATED_NAME = "Matemática II";

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private ICourseExistByService courseExistService;

    @Mock
    private ICourseGetByIdService courseGetByIdService;

    @InjectMocks
    private CourseUpdateService courseUpdateService;

    @Nested
    @DisplayName("cu14UpdateCourse")
    class Cu14UpdateCourse {

        @Test
        @DisplayName("Debe actualizar el curso cuando no hay conflicto de nombres")
        void shouldUpdateCourseWhenNoConflict() {
            // Given
            CourseUpdateDto request = buildUpdateDto(UPDATED_NAME);
            Course existingCourse = buildCourse(COURSE_ID, ORIGINAL_NAME);

            when(courseGetByIdService.findById(COURSE_ID)).thenReturn(existingCourse);
            when(courseRepository.save(any(Course.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            CourseResponseDto response = courseUpdateService.cu14UpdateCourse(COURSE_ID, request);

            // Then
            assertThat(response.getId()).isEqualTo(COURSE_ID);
            assertThat(response.getName()).isEqualTo(UPDATED_NAME);
            assertThat(response.getYear().getId()).isEqualTo(YEAR_ID);

            ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
            verify(courseExistService).validateExceptId(COURSE_ID, UPDATED_NAME, existingCourse.getYear());
            verify(courseRepository).save(courseCaptor.capture());

            Course savedCourse = courseCaptor.getValue();
            assertThat(savedCourse.getId()).isEqualTo(COURSE_ID);
            assertThat(savedCourse.getName()).isEqualTo(UPDATED_NAME);
            assertThat(savedCourse.getYear()).isEqualTo(existingCourse.getYear());
        }

        @Test
        @DisplayName("Debe propagar NotFoundException si el curso no existe")
        void shouldPropagateNotFoundWhenCourseDoesNotExist() {
            // Given
            CourseUpdateDto request = buildUpdateDto(UPDATED_NAME);
            NotFoundException notFound = new NotFoundException("Curso no encontrado");

            when(courseGetByIdService.findById(COURSE_ID)).thenThrow(notFound);

            // When + Then
            NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> courseUpdateService.cu14UpdateCourse(COURSE_ID, request)
            );

            assertThat(thrown).isSameAs(notFound);
        }

        @Test
        @DisplayName("Debe propagar ConflictException si el nombre ya existe en el mismo año")
        void shouldPropagateConflictWhenNameAlreadyExists() {
            // Given
            CourseUpdateDto request = buildUpdateDto(UPDATED_NAME);
            Course existingCourse = buildCourse(COURSE_ID, ORIGINAL_NAME);
            ConflictException conflict = new ConflictException("Curso ya existe.");

            when(courseGetByIdService.findById(COURSE_ID)).thenReturn(existingCourse);
            doThrow(conflict)
                .when(courseExistService)
                .validateExceptId(COURSE_ID, UPDATED_NAME, existingCourse.getYear());

            // When + Then
            ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> courseUpdateService.cu14UpdateCourse(COURSE_ID, request)
            );

            assertThat(thrown).isSameAs(conflict);
        }
    }

    private CourseUpdateDto buildUpdateDto(String name) {
        return CourseUpdateDto.builder()
            .name(name)
            .build();
    }

    private Course buildCourse(Long id, String name) {
        return Course.builder()
            .id(id)
            .name(name)
            .year(trinity.play2learn.backend.admin.year.models.Year.builder()
                .id(YEAR_ID)
                .name(YEAR_NAME)
                .build())
            .build();
    }
}

