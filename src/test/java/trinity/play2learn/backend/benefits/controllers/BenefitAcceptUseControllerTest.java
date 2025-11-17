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
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitAcceptUseService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitAcceptUseController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitAcceptUseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitAcceptUseService benefitAcceptUseService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("PATCH /benefits/teacher/accept-use/{id}")
    class AcceptUse {

        @Test
        @DisplayName("Given valid purchase ID When accepting use Then returns 200 with purchase response")
        void whenValidPurchaseId_returnsOk() throws Exception {
            // Given
            Long purchaseId = BenefitTestMother.DEFAULT_PURCHASE_ID;

            BenefitPurchaseSimpleResponseDto responseDto = BenefitPurchaseSimpleResponseDto.builder()
                .id(purchaseId)
                .state(BenefitPurchaseState.USED)
                .build();

            Mockito.when(benefitAcceptUseService.cu85AcceptBenefitUse(Mockito.any(), Mockito.eq(purchaseId)))
                .thenReturn(responseDto);

            // When & Then
            mockMvc.perform(patch("/benefits/teacher/accept-use/{id}", purchaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(purchaseId))
                .andExpect(jsonPath("$.data.state").value(BenefitPurchaseState.USED.name()))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitAcceptUseService).cu85AcceptBenefitUse(Mockito.any(), Mockito.eq(purchaseId));
        }

        @Test
        @DisplayName("Given non-existent purchase ID When accepting use Then service throws exception")
        void whenNonExistentPurchaseId_serviceThrowsException() throws Exception {
            // Given
            Long nonExistentId = 9999L;

            Mockito.when(benefitAcceptUseService.cu85AcceptBenefitUse(Mockito.any(), Mockito.eq(nonExistentId)))
                .thenThrow(new trinity.play2learn.backend.configs.exceptions.NotFoundException("Compra no encontrada"));

            // When & Then
            mockMvc.perform(patch("/benefits/teacher/accept-use/{id}", nonExistentId))
                .andExpect(status().isNotFound());

            Mockito.verify(benefitAcceptUseService).cu85AcceptBenefitUse(Mockito.any(), Mockito.eq(nonExistentId));
        }
    }
}

