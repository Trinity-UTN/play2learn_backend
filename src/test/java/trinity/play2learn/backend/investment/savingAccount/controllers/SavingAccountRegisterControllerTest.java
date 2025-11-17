package trinity.play2learn.backend.investment.savingAccount.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountRegisterRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountRegisterService;
import trinity.play2learn.backend.user.models.User;

@WebMvcTest(SavingAccountRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class SavingAccountRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISavingAccountRegisterService savingAccountRegisterService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /investment/saving-accounts")
    class PostSavingAccount {

        @Test
        @DisplayName("Given valid saving account request When registering Then returns 201 Created with saving account response")
        void whenValidRequest_returnsCreatedWithResponse() throws Exception {
            // Given - Request válido
            SavingAccountRegisterRequestDto requestDto = InvestmentTestMother
                    .savingAccountRegisterRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                            InvestmentTestMother.AMOUNT_MEDIUM);
            SavingAccountResponseDto responseDto = InvestmentTestMother.defaultSavingAccountResponseDto();

            Mockito.when(savingAccountRegisterService.cu102registerSavingAccount(Mockito.any(),
                    Mockito.any(User.class))).thenReturn(responseDto);

            // When & Then - POST debe retornar 201 con respuesta
            // Motivo: Verificar que el controller delega correctamente al servicio y retorna la respuesta esperada
            mockMvc.perform(post("/investment/saving-accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID))
                    .andExpect(jsonPath("$.data.name").value(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME))
                    .andExpect(jsonPath("$.data.initialAmount").value(InvestmentTestMother.AMOUNT_MEDIUM))
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(savingAccountRegisterService).cu102registerSavingAccount(Mockito.any(),
                    Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given invalid saving account request When registering Then returns 400 Bad Request")
        void whenInvalidRequest_returnsBadRequest() throws Exception {
            // Given - Request inválido (monto negativo)
            SavingAccountRegisterRequestDto requestDto = InvestmentTestMother
                    .savingAccountRegisterRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                            InvestmentTestMother.AMOUNT_NEGATIVE);

            // When & Then - POST debe retornar 400
            // Motivo: Verificar que las validaciones de Bean Validation funcionan correctamente
            mockMvc.perform(post("/investment/saving-accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given duplicate saving account name When registering Then returns 409 Conflict")
        void whenDuplicateName_returnsConflict() throws Exception {
            // Given - Request válido pero nombre duplicado
            SavingAccountRegisterRequestDto requestDto = InvestmentTestMother
                    .savingAccountRegisterRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                            InvestmentTestMother.AMOUNT_MEDIUM);

            Mockito.when(savingAccountRegisterService.cu102registerSavingAccount(Mockito.any(),
                    Mockito.any(User.class)))
                    .thenThrow(new ConflictException("Ya existe una caja de ahorro con ese nombre"));

            // When & Then - POST debe retornar 409
            // Motivo: Verificar que el controller maneja correctamente excepciones de negocio (ConflictException)
            mockMvc.perform(post("/investment/saving-accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isConflict());
        }
    }
}

