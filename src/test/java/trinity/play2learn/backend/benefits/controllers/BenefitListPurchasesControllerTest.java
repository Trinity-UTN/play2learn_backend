package trinity.play2learn.backend.benefits.controllers;

import java.util.Arrays;
import java.util.List;

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
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListPurchasesService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListPurchasesController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListPurchasesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListPurchasesService benefitListPurchasesService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/teacher/purchases/{id}")
    class ListPurchases {

        @Test
        @DisplayName("Given valid benefit ID with purchases When listing purchases Then returns 200 with list")
        void whenValidBenefitIdWithPurchases_returnsOkWithList() throws Exception {
            // Given
            Long benefitId = BenefitTestMother.DEFAULT_BENEFIT_ID;

            List<BenefitPurchaseSimpleResponseDto> purchases = Arrays.asList(
                BenefitPurchaseSimpleResponseDto.builder()
                    .id(BenefitTestMother.DEFAULT_PURCHASE_ID)
                    .state(BenefitPurchaseState.PURCHASED)
                    .benefitId(benefitId)
                    .build()
            );

            Mockito.when(benefitListPurchasesService.cu98ListPurchasesByBenefitId(Mockito.any(), Mockito.eq(benefitId)))
                .thenReturn(purchases);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/purchases/{id}", benefitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .andExpect(jsonPath("$.data[0].state").value(BenefitPurchaseState.PURCHASED.name()))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListPurchasesService).cu98ListPurchasesByBenefitId(Mockito.any(), Mockito.eq(benefitId));
        }

        @Test
        @DisplayName("Given valid benefit ID with no purchases When listing purchases Then returns 200 with empty list")
        void whenValidBenefitIdWithNoPurchases_returnsOkWithEmptyList() throws Exception {
            // Given
            Long benefitId = BenefitTestMother.DEFAULT_BENEFIT_ID;

            List<BenefitPurchaseSimpleResponseDto> emptyList = List.of();

            Mockito.when(benefitListPurchasesService.cu98ListPurchasesByBenefitId(Mockito.any(), Mockito.eq(benefitId)))
                .thenReturn(emptyList);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/purchases/{id}", benefitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListPurchasesService).cu98ListPurchasesByBenefitId(Mockito.any(), Mockito.eq(benefitId));
        }

        @Test
        @DisplayName("Given non-existent benefit ID When listing purchases Then service throws exception")
        void whenNonExistentBenefitId_serviceThrowsException() throws Exception {
            // Given
            Long nonExistentId = 9999L;

            Mockito.when(benefitListPurchasesService.cu98ListPurchasesByBenefitId(Mockito.any(), Mockito.eq(nonExistentId)))
                .thenThrow(new trinity.play2learn.backend.configs.exceptions.NotFoundException("Beneficio no encontrado"));

            // When & Then
            mockMvc.perform(get("/benefits/teacher/purchases/{id}", nonExistentId))
                .andExpect(status().isNotFound());

            Mockito.verify(benefitListPurchasesService).cu98ListPurchasesByBenefitId(Mockito.any(), Mockito.eq(nonExistentId));
        }
    }
}

