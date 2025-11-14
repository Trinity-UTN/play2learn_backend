package trinity.play2learn.backend.admin.course.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.year.models.Year;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseListService")
class CourseListServiceTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseListService courseListService;

    @Nested
    @DisplayName("cu9ListCourses")
    class Cu9ListCourses {

        @Test
        @DisplayName("Retorna cursos activos en forma de DTO ordenados según repositorio")
        void shouldReturnActiveCoursesAsDtoList() {
            // Given
            List<Course> courses = List.of(
                buildCourse(1L, "Matemática", "2025"),
                buildCourse(2L, "Lengua", "2025")
            );

            when(courseRepository.findAllByDeletedAtIsNull()).thenReturn(courses);

            // When
            List<CourseResponseDto> result = courseListService.cu9ListCourses();

            // Then
            verify(courseRepository).findAllByDeletedAtIsNull();
            assertThat(result)
                .hasSize(2)
                .extracting(CourseResponseDto::getName)
                .containsExactly("Matemática", "Lengua");
        }
    }

    private Course buildCourse(Long id, String name, String yearName) {
        return Course.builder()
            .id(id)
            .name(name)
            .year(Year.builder()
                .id(100L + id)
                .name(yearName)
                .build())
            .build();
    }
}

