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
        @DisplayName("Rechaza IDs no numéricos")
        void rejectsNonNumericId() {
            String invalidId = "abc";

            BadRequestException thrown = assertThrows(
                BadRequestException.class,
                () -> yearDeleteService.cu11deleteYear(invalidId)
            );

            assertThat(thrown.getMessage())
                .isEqualTo(BadRequestExceptionMessages.invalidFormat(invalidId));

            verifyNoInteractions(yearGetByIdService, courseExistByYearService, yearRepository);
        }

        @Test
        @DisplayName("Rechaza años ya eliminados lógicamente")
        void rejectsAlreadyDeletedYear() {
            String rawId = "10";
            Year deletedYear = buildYear(10L, "Primero Básico", LocalDateTime.now());
            stubExistingYear(rawId, deletedYear);

            BadRequestException thrown = assertThrows(
                BadRequestException.class,
                () -> yearDeleteService.cu11deleteYear(rawId)
            );

            assertThat(thrown.getMessage())
                .isEqualTo(ConflictExceptionMessages.resourceAlreadyDeleted(RESOURCE_NAME, rawId));

            verify(yearGetByIdService).findById(10L);
            verifyNoInteractions(courseExistByYearService);
            verify(yearRepository, never()).save(deletedYear);
        }

        @Test
        @DisplayName("Impide eliminar años con cursos asociados")
        void preventsDeletionWhenCoursesAreLinked() {
            String rawId = "7";
            Year yearWithCourses = buildYear(7L, "Segundo Básico");
            stubExistingYear(rawId, yearWithCourses);
            when(courseExistByYearService.validate(yearWithCourses)).thenReturn(true);

            ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> yearDeleteService.cu11deleteYear(rawId)
            );

            assertThat(thrown.getMessage())
                .isEqualTo(
                    ConflictExceptionMessages.resourceDeletionNotAllowedDueToAssociations(
                        RESOURCE_NAME,
                        rawId,
                        ASSOCIATION_NAME
                    )
                );

            verify(yearGetByIdService).findById(7L);
            verify(courseExistByYearService).validate(yearWithCourses);
            verify(yearRepository, never()).save(yearWithCourses);
        }

        @Test
        @DisplayName("Marca como eliminado y persiste cuando no hay restricciones")
        void softDeletesYearSuccessfully() {
            String rawId = "4";
            Year year = buildYear(4L, "Tercero Básico");
            stubExistingYear(rawId, year);
            when(courseExistByYearService.validate(year)).thenReturn(false);

            yearDeleteService.cu11deleteYear(rawId);

            assertThat(year.getDeletedAt()).isNotNull();

            ArgumentCaptor<Year> yearCaptor = ArgumentCaptor.forClass(Year.class);
            verify(yearRepository).save(yearCaptor.capture());
            Year savedYear = yearCaptor.getValue();

            assertThat(savedYear.getId()).isEqualTo(4L);
            assertThat(savedYear.getDeletedAt()).isNotNull();

            verify(yearGetByIdService).findById(4L);
            verify(courseExistByYearService).validate(year);
        }
    }

    private void stubExistingYear(String rawId, Year year) {
        when(yearGetByIdService.findById(Long.parseLong(rawId))).thenReturn(year);
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
