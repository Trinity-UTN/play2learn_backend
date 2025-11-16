package trinity.play2learn.backend.investment.savingAccount.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountDeleteService;
import trinity.play2learn.backend.user.models.User;

@WebMvcTest(SavingAccountDeleteController.class)
@AutoConfigureMockMvc(addFilters = false)
class SavingAccountDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISavingAccountDeleteService savingAccountDeleteService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("DELETE /investment/saving-accounts/{id}")
    class DeleteSavingAccount {

        @Test
        @DisplayName("Given valid saving account id When deleting Then returns 204 No Content")
        void whenValidId_returnsNoContent() throws Exception {
            // Given - ID válido
            Long id = InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID;

            Mockito.doNothing().when(savingAccountDeleteService).cu105deleteSavingAccount(
                    Mockito.eq(id), Mockito.any(User.class));

            // When & Then - DELETE debe retornar 204
            // Motivo: Verificar que el controller delega correctamente al servicio y retorna 204 No Content
            mockMvc.perform(delete("/investment/saving-accounts/{id}", id))
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(savingAccountDeleteService).cu105deleteSavingAccount(
                    Mockito.eq(id), Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given non-existing saving account id When deleting Then returns 404 Not Found")
        void whenNonExistingId_returnsNotFound() throws Exception {
            // Given - ID no existente
            Long id = InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID;

            Mockito.doThrow(new NotFoundException("No existe una caja de ahorro con el id proporcionado"))
                    .when(savingAccountDeleteService).cu105deleteSavingAccount(
                            Mockito.eq(id), Mockito.any(User.class));

            // When & Then - DELETE debe retornar 404
            // Motivo: Verificar que el controller maneja correctamente excepciones de recurso no encontrado (NotFoundException)
            mockMvc.perform(delete("/investment/saving-accounts/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Given saving account with balance When deleting Then returns 400 Bad Request")
        void whenAccountWithBalance_returnsBadRequest() throws Exception {
            // Given - Cuenta con balance
            Long id = InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID;

            Mockito.doThrow(new BadRequestException("No se puede eliminar una caja de ahorro con balance"))
                    .when(savingAccountDeleteService).cu105deleteSavingAccount(
                            Mockito.eq(id), Mockito.any(User.class));

            // When & Then - DELETE debe retornar 400
            // Motivo: Verificar que el controller maneja correctamente excepciones de validación de negocio (BadRequestException)
            mockMvc.perform(delete("/investment/saving-accounts/{id}", id))
                    .andExpect(status().isBadRequest());
        }
    }
}

