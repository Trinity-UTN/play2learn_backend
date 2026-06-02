package trinity.play2learn.backend.admin.year.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByYearService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.YearDeleteService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearGetByIdService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.BadRequestExceptionMessages;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearDeleteService")
class YearDeleteServiceTest {

    private static final String RESOURCE_NAME = "Año";
    private static final String ASSOCIATION_NAME = "Cursos";

    @Mock
    private IYearGetByIdService yearGetByIdService;

    @Mock
    private ICourseExistByYearService courseExistByYearService;

    @Mock
    private IYearRepository yearRepository;

    @InjectMocks
    private YearDeleteService yearDeleteService;

    @Nested
    @DisplayName("cu11deleteYear")
    class Cu11DeleteYear {

        @Test
        @DisplayName("Impide eliminar años con cursos asociados")
        void preventsDeletionWhenCoursesAreLinked() {
            Long rawId = 7L;
            Year yearWithCourses = buildYear(7L, "Segundo Básico");

            ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> yearDeleteService.cu11deleteYear(rawId)
            );

            assertThat(thrown.getMessage())
                .isEqualTo("El año no puede ser eliminado porque tiene cursos asociados");

            verify(yearGetByIdService).findById(7L);
            verify(courseExistByYearService).validate(yearWithCourses);
            verify(yearRepository, never()).save(yearWithCourses);
        }

    private Year buildYear(Long id, String name) {
        return buildYear(id, name, null);
    }

    private Year buildYear(Long id, String name, LocalDateTime deletedAt) {
        return Year.builder()
            .id(id)
            .name(name)
            .deletedAt(deletedAt)
            .build();
    }
  }
}
