package trinity.play2learn.backend.investment.fixedTermDeposit.controllers;

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
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositRegisterService;
import trinity.play2learn.backend.user.models.User;

@WebMvcTest(FixedTermDepositRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class FixedTermDepositRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFixedTermDepositRegisterService fixedTermDepositRegisterService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /investment/fixed-term-deposit")
    class PostFixedTermDeposit {

        @Test
        @DisplayName("Given valid fixed term deposit request When registering Then returns 201 Created with fixed term deposit response")
        void whenValidRequest_returnsCreatedWithResponse() throws Exception {
            // Given - Request válido
            FixedTermDepositRegisterRequestDto requestDto = InvestmentTestMother
                    .fixedTermDepositRegisterRequestDto(InvestmentTestMother.AMOUNT_MEDIUM,
                            trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays.MENSUAL);
            FixedTermDepositResponseDto responseDto = InvestmentTestMother.defaultFixedTermDepositResponseDto();

            Mockito.when(fixedTermDepositRegisterService.cu92registerFixedTermDeposit(Mockito.any(),
                    Mockito.any(User.class))).thenReturn(responseDto);

            // When & Then - POST debe retornar 201 con respuesta
            // Motivo: Verificar que el controller delega correctamente al servicio y retorna la respuesta esperada
            mockMvc.perform(post("/investment/fixed-term-deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID))
                    .andExpect(jsonPath("$.data.amountInvested").value(InvestmentTestMother.AMOUNT_MEDIUM))
                    .andExpect(jsonPath("$.data.amountReward").exists())
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(fixedTermDepositRegisterService).cu92registerFixedTermDeposit(Mockito.any(),
                    Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given invalid fixed term deposit request When registering Then returns 400 Bad Request")
        void whenInvalidRequest_returnsBadRequest() throws Exception {
            // Given - Request inválido (monto negativo)
            FixedTermDepositRegisterRequestDto requestDto = InvestmentTestMother
                    .fixedTermDepositRegisterRequestDto(InvestmentTestMother.AMOUNT_NEGATIVE,
                            trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays.MENSUAL);

            // When & Then - POST debe retornar 400
            // Motivo: Verificar que las validaciones de Bean Validation funcionan correctamente
            mockMvc.perform(post("/investment/fixed-term-deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given insufficient balance When registering Then returns 409 Conflict")
        void whenInsufficientBalance_returnsConflict() throws Exception {
            // Given - Request válido pero balance insuficiente
            FixedTermDepositRegisterRequestDto requestDto = InvestmentTestMother
                    .fixedTermDepositRegisterRequestDto(InvestmentTestMother.AMOUNT_LARGE,
                            trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays.MENSUAL);

            Mockito.when(fixedTermDepositRegisterService.cu92registerFixedTermDeposit(Mockito.any(),
                    Mockito.any(User.class)))
                    .thenThrow(new ConflictException("No hay suficiente balance en el wallet"));

            // When & Then - POST debe retornar 409
            // Motivo: Verificar que el controller maneja correctamente excepciones de negocio (ConflictException)
            mockMvc.perform(post("/investment/fixed-term-deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isConflict());
        }
    }
}

