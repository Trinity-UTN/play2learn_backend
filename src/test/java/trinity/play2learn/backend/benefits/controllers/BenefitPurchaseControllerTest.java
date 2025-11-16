package trinity.play2learn.backend.benefits.controllers;

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

import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseResponseDto;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitPurchaseService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitPurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitPurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitPurchaseService benefitPurchaseService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /benefits/student/purchase")
    class PurchaseBenefit {

        @Test
        @DisplayName("Given valid purchase request When purchasing benefit Then returns 201 Created with purchase response")
        void whenValidRequest_returnsCreated() throws Exception {
            // Given - Request DTO válido con benefitId
            BenefitPurchaseRequestDto requestDto = BenefitTestMother.benefitPurchaseRequest(BenefitTestMother.DEFAULT_BENEFIT_ID);

            // Mock - Servicio retorna compra creada con estado PURCHASED
            BenefitPurchaseResponseDto responseDto = BenefitPurchaseResponseDto.builder()
                .id(BenefitTestMother.DEFAULT_PURCHASE_ID)
                .state(BenefitPurchaseState.PURCHASED)
                .build();

            Mockito.when(benefitPurchaseService.cu75PurchaseBenefit(Mockito.any(BenefitPurchaseRequestDto.class), Mockito.any()))
                .thenReturn(responseDto);

            // When & Then - POST /benefits/student/purchase debe retornar 201 con datos de la compra
            mockMvc.perform(post("/benefits/student/purchase")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .andExpect(jsonPath("$.data.state").value(BenefitPurchaseState.PURCHASED.name()))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(benefitPurchaseService).cu75PurchaseBenefit(Mockito.any(BenefitPurchaseRequestDto.class), Mockito.any());
        }

        @Test
        @DisplayName("Given invalid request with null benefitId When purchasing benefit Then returns 400 Bad Request")
        void whenInvalidRequestWithNullBenefitId_returnsBadRequest() throws Exception {
            // Given - Request DTO inválido: benefitId es null
            BenefitPurchaseRequestDto requestDto = BenefitPurchaseRequestDto.builder()
                .benefitId(null)
                .build();

            // When & Then - Validación de Bean Validation debe rechazar la petición
            mockMvc.perform(post("/benefits/student/purchase")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

            // Verify - Servicio no debe ser llamado por validación fallida
            Mockito.verifyNoInteractions(benefitPurchaseService);
        }

        @Test
        @DisplayName("Given non-existent benefit ID When purchasing benefit Then returns 404 Not Found")
        void whenNonExistentBenefit_serviceThrowsException() throws Exception {
            // Given - Request DTO con ID de beneficio inexistente
            Long nonExistentBenefitId = 9999L;
            BenefitPurchaseRequestDto requestDto = BenefitTestMother.benefitPurchaseRequest(nonExistentBenefitId);

            // Mock - Servicio lanza NotFoundException
            Mockito.when(benefitPurchaseService.cu75PurchaseBenefit(Mockito.any(BenefitPurchaseRequestDto.class), Mockito.any()))
                .thenThrow(new trinity.play2learn.backend.configs.exceptions.NotFoundException("Beneficio no encontrado"));

            // When & Then - Debe retornar 404 cuando el beneficio no existe
            mockMvc.perform(post("/benefits/student/purchase")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

            // Verify - Servicio fue llamado pero lanzó excepción
            Mockito.verify(benefitPurchaseService).cu75PurchaseBenefit(Mockito.any(BenefitPurchaseRequestDto.class), Mockito.any());
        }
    }
}

