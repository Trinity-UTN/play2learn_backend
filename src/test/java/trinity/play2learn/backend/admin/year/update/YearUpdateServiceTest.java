package trinity.play2learn.backend.admin.year.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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

import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.dtos.YearUpdateRequestDto;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.YearUpdateService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearExistService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearUpdateService")
class YearUpdateServiceTest {

    private static final String RESOURCE_NAME = "Año";
    private static final Long EXISTING_ID = 5L;
    private static final Long MISSING_ID = 99L;

    @Mock
    private IYearExistService yearExistService;

    @Mock
    private IYearGetByIdService yearGetByIdService;

    @Mock
    private IYearRepository yearRepository;

    @InjectMocks
    private YearUpdateService yearUpdateService;

    @Nested
    @DisplayName("cu10UpdateYear")
    class Cu10UpdateYear {

        @Test
        @DisplayName("Debe actualizar el nombre del año cuando el id existe y el nombre es único")
        void shouldUpdateYearWhenNameIsUnique() {
            // Arrange
            Year existingYear = buildYear(EXISTING_ID, "Primero Básico");
            YearUpdateRequestDto request = buildRequest("Segundo Básico");

            when(yearGetByIdService.findById(EXISTING_ID)).thenReturn(existingYear);
            when(yearRepository.save(any(Year.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            YearResponseDto response = yearUpdateService.cu10UpdateYear(EXISTING_ID, request);

            // Assert
            verify(yearExistService).validateExceptId(request.getName(), EXISTING_ID);
            assertThat(existingYear.getName()).isEqualTo(request.getName());

            ArgumentCaptor<Year> yearCaptor = ArgumentCaptor.forClass(Year.class);
            verify(yearRepository).save(yearCaptor.capture());
            assertThat(yearCaptor.getValue().getName()).isEqualTo(request.getName());

            assertThat(response.getId()).isEqualTo(EXISTING_ID);
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        @DisplayName("Debe lanzar ConflictException cuando el nuevo nombre ya está en uso en otro año")
        void shouldThrowConflictWhenNameAlreadyExists() {
            // Arrange
            Year existingYear = buildYear(EXISTING_ID, "Primero Básico");
            YearUpdateRequestDto request = buildRequest("Duplicado");

            when(yearGetByIdService.findById(EXISTING_ID)).thenReturn(existingYear);
            doThrow(new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExistsByName(RESOURCE_NAME, request.getName())
            )).when(yearExistService).validateExceptId(request.getName(), EXISTING_ID);

            // Act + Assert
            ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> yearUpdateService.cu10UpdateYear(EXISTING_ID, request)
            );

            assertThat(thrown.getMessage())
                .isEqualTo(ConflictExceptionMessages.resourceAlreadyExistsByName(RESOURCE_NAME, request.getName()));

            verify(yearGetByIdService).findById(EXISTING_ID);
            verify(yearExistService).validateExceptId(request.getName(), EXISTING_ID);
            verify(yearRepository, never()).save(any(Year.class));
        }

        @Test
        @DisplayName("Debe lanzar NotFoundException cuando el año a actualizar no existe")
        void shouldThrowNotFoundWhenYearDoesNotExist() {
            // Arrange
            YearUpdateRequestDto request = buildRequest("Algún nombre");

            when(yearGetByIdService.findById(MISSING_ID)).thenThrow(new NotFoundException(
                NotFoundExceptionMesagges.resourceNotFoundById(RESOURCE_NAME, String.valueOf(MISSING_ID))
            ));

            // Act + Assert
            NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> yearUpdateService.cu10UpdateYear(MISSING_ID, request)
            );

            assertThat(thrown.getMessage())
                .isEqualTo(NotFoundExceptionMesagges.resourceNotFoundById(RESOURCE_NAME, String.valueOf(MISSING_ID)));

            verify(yearGetByIdService).findById(MISSING_ID);
            verifyNoInteractions(yearExistService);
            verifyNoInteractions(yearRepository);
        }
    }

    private Year buildYear(Long id, String name) {
        return Year.builder()
            .id(id)
            .name(name)
            .build();
    }

    private YearUpdateRequestDto buildRequest(String name) {
        return YearUpdateRequestDto.builder()
            .name(name)
            .build();
    }
}

