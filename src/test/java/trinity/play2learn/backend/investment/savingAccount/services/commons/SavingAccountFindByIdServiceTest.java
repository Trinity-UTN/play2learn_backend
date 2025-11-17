package trinity.play2learn.backend.investment.savingAccount.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;

@ExtendWith(MockitoExtension.class)
class SavingAccountFindByIdServiceTest {

    @Mock
    private ISavingAccountRepository savingAccountRepository;

    private SavingAccountFindByIdService savingAccountFindByIdService;

    @BeforeEach
    void setUp() {
        savingAccountFindByIdService = new SavingAccountFindByIdService(savingAccountRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given valid saving account id When finding by id Then returns saving account")
        void whenValidId_returnsSavingAccount() {
            // Given - ID válido
            Long id = InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID;
            SavingAccount expectedAccount = InvestmentTestMother.defaultSavingAccount();

            when(savingAccountRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(expectedAccount));

            // When - Buscar por ID
            SavingAccount result = savingAccountFindByIdService.execute(id);

            // Then - Debe retornar caja de ahorro
            // Motivo: Verificar que el servicio retorna la entidad correcta y que el ID coincide
            verify(savingAccountRepository).findByIdAndDeletedAtIsNull(id);
            assertThat(result)
                .isEqualTo(expectedAccount)
                .extracting(SavingAccount::getId)
                .isEqualTo(id);
        }

        @Test
        @DisplayName("Given non-existing saving account id When finding by id Then throws NotFoundException")
        void whenNonExistingId_throwsNotFoundException() {
            // Given - ID no existente
            // Motivo: Validar que el servicio maneja correctamente IDs que no existen en la base de datos
            Long id = 999L;

            when(savingAccountRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

            // When & Then - Debe lanzar NotFoundException
            assertThatThrownBy(() -> savingAccountFindByIdService.execute(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No existe una caja de ahorro con el id proporcionado");

            verify(savingAccountRepository).findByIdAndDeletedAtIsNull(id);
        }

        @Test
        @DisplayName("Given deleted saving account id When finding by id Then throws NotFoundException")
        void whenDeletedAccountId_throwsNotFoundException() {
            // Given - ID de caja de ahorro eliminada
            // Motivo: Validar que el servicio respeta el soft delete y no retorna entidades eliminadas
            Long id = InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID;

            when(savingAccountRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

            // When & Then - Debe lanzar NotFoundException
            assertThatThrownBy(() -> savingAccountFindByIdService.execute(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No existe una caja de ahorro con el id proporcionado");

            verify(savingAccountRepository).findByIdAndDeletedAtIsNull(id);
        }
    }
}

