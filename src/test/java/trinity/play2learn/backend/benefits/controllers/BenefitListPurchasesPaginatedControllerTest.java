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
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListPurchasesPaginatedService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.configs.response.PaginatedData;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListPurchasesPaginatedController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListPurchasesPaginatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListPurchasesPaginatedService benefitListPurchasesPaginatedService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/teacher/purchases/paginated/{id}")
    class ListPurchasesPaginated {

        @Test
        @DisplayName("Given valid benefit ID with purchases When listing paginated Then returns 200 with paginated data")
        void whenValidBenefitIdWithPurchases_returnsOkWithPaginatedData() throws Exception {
            // Given
            Long benefitId = BenefitTestMother.DEFAULT_BENEFIT_ID;

            List<BenefitPurchaseSimpleResponseDto> purchases = Arrays.asList(
                BenefitPurchaseSimpleResponseDto.builder()
                    .id(BenefitTestMother.DEFAULT_PURCHASE_ID)
                    .state(BenefitPurchaseState.PURCHASED)
                    .build()
            );

            PaginatedData<BenefitPurchaseSimpleResponseDto> paginatedData = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                .results(purchases)
                .count(1)
                .totalPages(1)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListPurchasesPaginatedService.cu101ListPurchasesPaginated(
                Mockito.any(), Mockito.eq(benefitId), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/purchases/paginated/{id}", benefitId)
                    .param("page", "1")
                    .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListPurchasesPaginatedService).cu101ListPurchasesPaginated(
                Mockito.any(), Mockito.eq(benefitId), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Given valid benefit ID with no purchases When listing paginated Then returns 200 with empty paginated data")
        void whenValidBenefitIdWithNoPurchases_returnsOkWithEmptyPaginatedData() throws Exception {
            // Given
            Long benefitId = BenefitTestMother.DEFAULT_BENEFIT_ID;

            PaginatedData<BenefitPurchaseSimpleResponseDto> paginatedData = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListPurchasesPaginatedService.cu101ListPurchasesPaginated(
                Mockito.any(), Mockito.eq(benefitId), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/purchases/paginated/{id}", benefitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results").isEmpty())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListPurchasesPaginatedService).cu101ListPurchasesPaginated(
                Mockito.any(), Mockito.eq(benefitId), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }
    }
}

