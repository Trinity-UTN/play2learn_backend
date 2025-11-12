package trinity.play2learn.backend.admin.course.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.dtos.CourseRequestDto;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.commons.CourseExistByService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.services.commons.YearGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseRegisterService")
class CourseRegisterServiceTest {

    private static final String COURSE_NAME = "Matemática";
    private static final String YEAR_NAME = "2025";
    private static final String RESOURCE_NAME = "Curso";
    private static final long YEAR_ID = 2025L;
    private static final long GENERATED_ID = 10L;

    @Mock
    private YearGetByIdService yearGetByIdService;

    @Mock
    private CourseExistByService courseExistService;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseRegisterService courseRegisterService;

    @Nested
    @DisplayName("cu6RegisterCourse")
    class Cu6RegisterCourse {

        @Test
        @DisplayName("Debe registrar un curso nuevo cuando el nombre no existe en el año")
        void shouldRegisterCourseWhenNameIsAvailable() {
            // Given
            CourseRequestDto request = buildRequestDto();
            Year academicYear = buildYear(YEAR_ID, YEAR_NAME);

            when(yearGetByIdService.findById(YEAR_ID)).thenReturn(academicYear);
            when(courseExistService.validate(COURSE_NAME, academicYear)).thenReturn(false);
            when(courseRepository.save(any(Course.class)))
                .thenAnswer(invocation -> {
                    Course toPersist = invocation.getArgument(0);
                    return Course.builder()
                        .id(GENERATED_ID)
                        .name(toPersist.getName())
                        .year(toPersist.getYear())
                        .build();
                });

            // When
            CourseResponseDto response = courseRegisterService.cu6RegisterCourse(request);

            // Then
            assertThat(response.getId()).isEqualTo(GENERATED_ID);
            assertThat(response.getName()).isEqualTo(COURSE_NAME);
            assertThat(response.getYear().getId()).isEqualTo(YEAR_ID);

            ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
            verify(courseRepository).save(courseCaptor.capture());

            Course savedCourse = courseCaptor.getValue();
            assertThat(savedCourse.getName()).isEqualTo(COURSE_NAME);
            assertThat(savedCourse.getYear()).isEqualTo(academicYear);

            verify(courseExistService).validate(COURSE_NAME, academicYear);
            verify(yearGetByIdService).findById(YEAR_ID);
        }

        @Test
        @DisplayName("Debe lanzar ConflictException cuando el curso ya existe en el año")
        void shouldThrowConflictWhenCourseAlreadyExists() {
            // Given
            CourseRequestDto request = buildRequestDto();
            Year academicYear = buildYear(YEAR_ID, YEAR_NAME);

            when(yearGetByIdService.findById(YEAR_ID)).thenReturn(academicYear);
            when(courseExistService.validate(COURSE_NAME, academicYear)).thenReturn(true);

            // When + Then
            ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> courseRegisterService.cu6RegisterCourse(request)
            );

            assertThat(thrown.getMessage())
                .isEqualTo(ConflictExceptionMessages.resourceAlreadyExists(RESOURCE_NAME));

            verify(courseExistService).validate(COURSE_NAME, academicYear);
            verify(yearGetByIdService).findById(YEAR_ID);
            verifyNoInteractions(courseRepository);
        }

        @Test
        @DisplayName("Debe propagar NotFoundException cuando el año no existe")
        void shouldPropagateNotFoundWhenYearDoesNotExist() {
            // Given
            CourseRequestDto request = buildRequestDto();
            NotFoundException notFound = new NotFoundException("Año no encontrado");

            when(yearGetByIdService.findById(YEAR_ID)).thenThrow(notFound);

            // When + Then
            NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> courseRegisterService.cu6RegisterCourse(request)
            );

            assertThat(thrown).isSameAs(notFound);

            verify(yearGetByIdService).findById(YEAR_ID);
            verifyNoInteractions(courseExistService, courseRepository);
        }
    }

    private CourseRequestDto buildRequestDto() {
        return CourseRequestDto.builder()
            .name(COURSE_NAME)
            .year_id(YEAR_ID)
            .build();
    }

    private Year buildYear(long id, String name) {
        return Year.builder()
            .id(id)
            .name(name)
            .build();
    }
}

