package trinity.play2learn.backend.admin.year.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.commons.YearExistService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearExistService")
class YearExistServiceTest {

    private static final String RESOURCE_NAME = "Año";
    private static final String NAME = "Primero Básico";
    private static final Long ID = 5L;

    @Mock
    private IYearRepository yearRepository;

    @InjectMocks
    private YearExistService yearExistService;

    @Nested
    @DisplayName("validate(name)")
    class ValidateByName {

        @Test
        @DisplayName("Devuelve true cuando el nombre ya existe (comparación case-insensitive)")
        void returnsTrueWhenNameExists() {
            when(yearRepository.existsByNameIgnoreCase(NAME)).thenReturn(true);

            boolean exists = yearExistService.validate(NAME);

            assertThat(exists).isTrue();
            verify(yearRepository).existsByNameIgnoreCase(NAME);
        }

        @Test
        @DisplayName("Devuelve false cuando el nombre no existe")
        void returnsFalseWhenNameDoesNotExist() {
            when(yearRepository.existsByNameIgnoreCase(NAME)).thenReturn(false);

            boolean exists = yearExistService.validate(NAME);

            assertThat(exists).isFalse();
            verify(yearRepository).existsByNameIgnoreCase(NAME);
        }
    }

    @Nested
    @DisplayName("validate(id)")
    class ValidateById {

        @Test
        @DisplayName("Devuelve true cuando existe un año con el ID indicado")
        void returnsTrueWhenIdExists() {
            when(yearRepository.existsById(ID)).thenReturn(true);

            boolean exists = yearExistService.validate(ID);

            assertThat(exists).isTrue();
            verify(yearRepository).existsById(ID);
        }

        @Test
        @DisplayName("Devuelve false cuando no existe un año con el ID indicado")
        void returnsFalseWhenIdDoesNotExist() {
            when(yearRepository.existsById(ID)).thenReturn(false);

            boolean exists = yearExistService.validate(ID);

            assertThat(exists).isFalse();
            verify(yearRepository).existsById(ID);
        }
    }

    @Nested
    @DisplayName("validateExceptId")
    class ValidateExceptId {

        @Test
        @DisplayName("No lanza excepción cuando el nombre solo coincide con el mismo ID")
        void doesNotThrowWhenNameBelongsToSameId() {
            when(yearRepository.existsByNameIgnoreCaseAndIdNot(NAME, ID)).thenReturn(false);

            yearExistService.validateExceptId(NAME, ID);

            verify(yearRepository).existsByNameIgnoreCaseAndIdNot(NAME, ID);
        }

        @Test
        @DisplayName("Lanza ConflictException cuando el nombre ya existe en otro año")
        void throwsConflictWhenNameBelongsToDifferentId() {
            when(yearRepository.existsByNameIgnoreCaseAndIdNot(NAME, ID)).thenReturn(true);

            assertThatThrownBy(() -> yearExistService.validateExceptId(NAME, ID))
                .isInstanceOf(ConflictException.class)
                .hasMessage(ConflictExceptionMessages.resourceAlreadyExistsByName(RESOURCE_NAME, NAME));

            verify(yearRepository).existsByNameIgnoreCaseAndIdNot(NAME, ID);
        }
    }
}

