package trinity.play2learn.backend.admin.course.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseCommonServices")
class CourseCommonServicesTest {

    private static final String COURSE_NAME = "Matemática";
    private static final long COURSE_ID = 9L;
    private static final long YEAR_ID = 2025L;

    @Mock
    private ICourseRepository courseRepository;

    private CourseExistByService courseExistByService;
    private CourseGetByIdService courseGetByIdService;
    private CourseExistByYearService courseExistByYearService;

    @BeforeEach
    void setUp() {
        courseExistByService = new CourseExistByService(courseRepository);
        courseGetByIdService = new CourseGetByIdService(courseRepository);
        courseExistByYearService = new CourseExistByYearService(courseRepository);
    }

    @Nested
    @DisplayName("CourseExistByService")
    class CourseExistByServiceSpec {

        private Year year;

        @BeforeEach
        void setUp() {
            year = buildYear();
        }

        @Test
        @DisplayName("validate(String, Year) debe delegar en el repositorio")
        void shouldValidateByNameAndYear() {
            when(courseRepository.existsByNameIgnoreCaseAndYear(COURSE_NAME, year)).thenReturn(true);

            boolean exists = courseExistByService.validate(COURSE_NAME, year);

            assertThat(exists).isTrue();
            verify(courseRepository).existsByNameIgnoreCaseAndYear(COURSE_NAME, year);
        }

        @Test
        @DisplayName("validate(Long) debe devolver el resultado del repositorio")
        void shouldValidateById() {
            when(courseRepository.existsById(COURSE_ID)).thenReturn(false);

            boolean exists = courseExistByService.validate(COURSE_ID);

            assertThat(exists).isFalse();
            verify(courseRepository).existsById(COURSE_ID);
        }

        @Test
        @DisplayName("validate(String) debe delegar en existsByName")
        void shouldValidateByName() {
            when(courseRepository.existsByName(COURSE_NAME)).thenReturn(true);

            boolean exists = courseExistByService.validate(COURSE_NAME);

            assertThat(exists).isTrue();
            verify(courseRepository).existsByName(COURSE_NAME);
        }

        @Test
        @DisplayName("validateExceptId debe lanzar ConflictException cuando ya existe el nombre en el mismo año")
        void shouldThrowConflictWhenNameExistsForAnotherCourse() {
            when(courseRepository.existsByNameIgnoreCaseAndYearAndIdNot(COURSE_NAME, year, COURSE_ID)).thenReturn(true);

            assertThatThrownBy(() -> courseExistByService.validateExceptId(COURSE_ID, COURSE_NAME, year))
                .isInstanceOf(ConflictException.class)
                .hasMessage(ConflictExceptionMessages.resourceAlreadyExists("Curso"));
        }

        @Test
        @DisplayName("validateExceptId no debe lanzar excepciones cuando no existe duplicado")
        void shouldNotThrowWhenNameIsAvailable() {
            when(courseRepository.existsByNameIgnoreCaseAndYearAndIdNot(COURSE_NAME, year, COURSE_ID)).thenReturn(false);

            assertThatCode(() -> courseExistByService.validateExceptId(COURSE_ID, COURSE_NAME, year))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("CourseGetByIdService")
    class CourseGetByIdServiceSpec {

        private Course course;

        @BeforeEach
        void setUp() {
            course = buildCourse();
        }

        @Test
        @DisplayName("Debe devolver el curso cuando existe y no está eliminado")
        void shouldReturnCourseWhenExists() {
            when(courseRepository.findByIdAndDeletedAtIsNull(COURSE_ID)).thenReturn(Optional.of(course));

            Course found = courseGetByIdService.findById(COURSE_ID);

            assertThat(found).isSameAs(course);
            verify(courseRepository).findByIdAndDeletedAtIsNull(COURSE_ID);
        }

        @Test
        @DisplayName("Debe lanzar NotFoundException cuando el curso no existe o está eliminado")
        void shouldThrowNotFoundWhenCourseIsMissing() {
            when(courseRepository.findByIdAndDeletedAtIsNull(COURSE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseGetByIdService.findById(COURSE_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NotFoundExceptionMesagges.resourceNotFoundById("Curso", String.valueOf(COURSE_ID)));
        }
    }

    @Nested
    @DisplayName("CourseExistByYearService")
    class CourseExistByYearServiceSpec {

        @Test
        @DisplayName("Debe delegar en existsByYearIdAndDeletedAtIsNull")
        void shouldValidateByYear() {
            Year year = buildYear();
            when(courseRepository.existsByYearIdAndDeletedAtIsNull(YEAR_ID)).thenReturn(true);

            boolean exists = courseExistByYearService.validate(year);

            assertThat(exists).isTrue();
            verify(courseRepository).existsByYearIdAndDeletedAtIsNull(YEAR_ID);
        }
    }

    private Year buildYear() {
        return Year.builder()
            .id(YEAR_ID)
            .name("2025")
            .build();
    }

    private Course buildCourse() {
        return Course.builder()
            .id(COURSE_ID)
            .name(COURSE_NAME)
            .year(buildYear())
            .build();
    }
}

