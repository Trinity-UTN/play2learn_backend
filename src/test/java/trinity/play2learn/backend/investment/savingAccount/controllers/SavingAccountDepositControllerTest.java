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

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountDepositRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountDepositService;
import trinity.play2learn.backend.user.models.User;

@WebMvcTest(SavingAccountDepositController.class)
@AutoConfigureMockMvc(addFilters = false)
class SavingAccountDepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISavingAccountDepositService savingAccountDepositService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /investment/saving-accounts/deposit")
    class PostDeposit {

        @Test
        @DisplayName("Given valid deposit request When depositing Then returns 201 Created with updated saving account response")
        void whenValidRequest_returnsCreatedWithResponse() throws Exception {
            // Given - Request válido
            SavingAccountDepositRequestDto requestDto = InvestmentTestMother
                    .savingAccountDepositRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID,
                            InvestmentTestMother.AMOUNT_MEDIUM);
            SavingAccountResponseDto responseDto = InvestmentTestMother.defaultSavingAccountResponseDto();

            Mockito.when(savingAccountDepositService.cu103depositSavingAccount(Mockito.any(),
                    Mockito.any(User.class))).thenReturn(responseDto);

            // When & Then - POST debe retornar 201 con respuesta
            mockMvc.perform(post("/investment/saving-accounts/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID))
                    .andExpect(jsonPath("$.data.currentAmount").exists())
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(savingAccountDepositService).cu103depositSavingAccount(Mockito.any(),
                    Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given invalid deposit request When depositing Then returns 400 Bad Request")
        void whenInvalidRequest_returnsBadRequest() throws Exception {
            // Given - Request inválido (monto negativo)
            SavingAccountDepositRequestDto requestDto = InvestmentTestMother
                    .savingAccountDepositRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID,
                            InvestmentTestMother.AMOUNT_NEGATIVE);

            // When & Then - POST debe retornar 400
            mockMvc.perform(post("/investment/saving-accounts/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given non-existing saving account When depositing Then returns 404 Not Found")
        void whenNonExistingAccount_returnsNotFound() throws Exception {
            // Given - Request válido pero cuenta no existe
            SavingAccountDepositRequestDto requestDto = InvestmentTestMother
                    .savingAccountDepositRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID,
                            InvestmentTestMother.AMOUNT_MEDIUM);

            Mockito.when(savingAccountDepositService.cu103depositSavingAccount(Mockito.any(),
                    Mockito.any(User.class)))
                    .thenThrow(new NotFoundException("No existe una caja de ahorro con el id proporcionado"));

            // When & Then - POST debe retornar 404
            mockMvc.perform(post("/investment/saving-accounts/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Given insufficient wallet balance When depositing Then returns 400 Bad Request")
        void whenInsufficientBalance_returnsBadRequest() throws Exception {
            // Given - Request válido pero balance insuficiente
            SavingAccountDepositRequestDto requestDto = InvestmentTestMother
                    .savingAccountDepositRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID,
                            InvestmentTestMother.AMOUNT_LARGE);

            Mockito.when(savingAccountDepositService.cu103depositSavingAccount(Mockito.any(),
                    Mockito.any(User.class)))
                    .thenThrow(new BadRequestException("No hay suficiente balance en el wallet"));

            // When & Then - POST debe retornar 400
            mockMvc.perform(post("/investment/saving-accounts/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}

