package trinity.play2learn.backend.benefits.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitRequestUseService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitRequestUseController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitRequestUseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitRequestUseService benefitRequestUseService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("PATCH /benefits/student/request-use/{id}")
    class RequestUse {

        @Test
        @DisplayName("Given valid purchase ID When requesting use Then returns 200 with purchase response")
        void whenValidPurchaseId_returnsOk() throws Exception {
            // Given
            Long purchaseId = BenefitTestMother.DEFAULT_PURCHASE_ID;

            BenefitPurchaseSimpleResponseDto responseDto = BenefitPurchaseSimpleResponseDto.builder()
                .id(purchaseId)
                .state(BenefitPurchaseState.USE_REQUESTED)
                .build();

            Mockito.when(benefitRequestUseService.cu81RequestBenefitUse(Mockito.any(), Mockito.eq(purchaseId)))
                .thenReturn(responseDto);

            // When & Then
            mockMvc.perform(patch("/benefits/student/request-use/{id}", purchaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(purchaseId))
                .andExpect(jsonPath("$.data.state").value(BenefitPurchaseState.USE_REQUESTED.name()))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitRequestUseService).cu81RequestBenefitUse(Mockito.any(), Mockito.eq(purchaseId));
        }

        @Test
        @DisplayName("Given non-existent purchase ID When requesting use Then service throws exception")
        void whenNonExistentPurchaseId_serviceThrowsException() throws Exception {
            // Given
            Long nonExistentId = 9999L;

            Mockito.when(benefitRequestUseService.cu81RequestBenefitUse(Mockito.any(), Mockito.eq(nonExistentId)))
                .thenThrow(new trinity.play2learn.backend.configs.exceptions.NotFoundException("Compra no encontrada"));

            // When & Then
            mockMvc.perform(patch("/benefits/student/request-use/{id}", nonExistentId))
                .andExpect(status().isNotFound());

            Mockito.verify(benefitRequestUseService).cu81RequestBenefitUse(Mockito.any(), Mockito.eq(nonExistentId));
        }
    }
}

