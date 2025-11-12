package trinity.play2learn.backend.admin.course.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.year.models.Year;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseGetService")
class CourseGetServiceTest {

    private static final Long COURSE_ID = 11L;
    private static final Long YEAR_ID = 2025L;
    private static final String COURSE_NAME = "Matemática I";
    private static final String YEAR_NAME = "2025";

    @Mock
    private ICourseGetByIdService courseGetByIdService;

    @InjectMocks
    private CourseGetService courseGetService;

    private Course existingCourse;

    @BeforeEach
    void setUp() {
        existingCourse = buildCourse();
    }

    @Nested
    @DisplayName("cu17GetCourse")
    class Cu17GetCourse {

        @Test
        @DisplayName("Delegar en CourseGetByIdService y mapear a DTO")
        void shouldDelegateAndMapToDto() {
            // Given
            when(courseGetByIdService.findById(COURSE_ID)).thenReturn(existingCourse);

            // When
            CourseResponseDto response = courseGetService.cu17GetCourse(COURSE_ID);

            // Then
            verify(courseGetByIdService).findById(COURSE_ID);
            assertThat(response.getId()).isEqualTo(COURSE_ID);
            assertThat(response.getName()).isEqualTo(COURSE_NAME);
            assertThat(response.getYear().getId()).isEqualTo(YEAR_ID);
            assertThat(response.getYear().getName()).isEqualTo(YEAR_NAME);
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

