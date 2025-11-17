package trinity.play2learn.backend.admin.year.register;

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

import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.YearRegisterService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearExistService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearRegisterService")
class YearRegisterServiceTest {

    private static final String RESOURCE_NAME = "Año";
    private static final long GENERATED_ID = 10L;

    @Mock
    private IYearExistService yearExistService;

    @Mock
    private IYearRepository yearRepository;

    @InjectMocks
    private YearRegisterService yearRegisterService;

    @Nested
    @DisplayName("cu7RegisterYear")
    class Cu7RegisterYear {

        @Test
        @DisplayName("Debe registrar un año cuando el nombre no existe")
        void shouldRegisterYearWhenNameIsAvailable() {
            // Arrange
            YearRequestDto request = buildRequest("Primero Básico");

            when(yearExistService.validate(request.getName())).thenReturn(false);
            when(yearRepository.save(any(Year.class)))
                .thenAnswer(invocation -> {
                    Year yearToPersist = invocation.getArgument(0);
                    return Year.builder()
                        .id(GENERATED_ID)
                        .name(yearToPersist.getName())
                        .deletedAt(yearToPersist.getDeletedAt())
                        .build();
                });

            // Act
            YearResponseDto response = yearRegisterService.cu7RegisterYear(request);

            // Assert
            assertThat(response.getId()).isEqualTo(GENERATED_ID);
            assertThat(response.getName()).isEqualTo(request.getName());

            ArgumentCaptor<Year> yearCaptor = ArgumentCaptor.forClass(Year.class);
            verify(yearRepository).save(yearCaptor.capture());
            Year savedYear = yearCaptor.getValue();

            assertThat(savedYear.getName()).isEqualTo(request.getName());
            assertThat(savedYear.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("Debe lanzar ConflictException cuando el nombre ya existe")
        void shouldThrowConflictWhenNameAlreadyExists() {
            // Arrange
            YearRequestDto request = buildRequest("Primero Básico");

            when(yearExistService.validate(request.getName())).thenReturn(true);

            // Act + Assert
            ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> yearRegisterService.cu7RegisterYear(request)
            );

            assertThat(thrown.getMessage())
                .isEqualTo(ConflictExceptionMessages.resourceAlreadyExistsByName(RESOURCE_NAME, request.getName()));

            verify(yearExistService).validate(request.getName());
            verifyNoInteractions(yearRepository);
        }
    }

    private YearRequestDto buildRequest(String name) {
        return YearRequestDto.builder()
            .name(name)
            .build();
    }
}

