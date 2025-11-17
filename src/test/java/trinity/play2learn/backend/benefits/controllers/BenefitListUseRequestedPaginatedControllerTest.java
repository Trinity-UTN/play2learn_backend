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
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUseRequestedPaginatedService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.configs.response.PaginatedData;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListUseRequestedPaginatedController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListUseRequestedPaginatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListUseRequestedPaginatedService benefitListUseRequestedPaginatedService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/teacher/use-requested/paginated")
    class ListUseRequestedPaginated {

        @Test
        @DisplayName("Given teacher with use requested purchases When listing paginated Then returns 200 with paginated data")
        void whenTeacherHasUseRequested_returnsOkWithPaginatedData() throws Exception {
            // Given
            List<BenefitPurchaseSimpleResponseDto> purchases = Arrays.asList(
                BenefitPurchaseSimpleResponseDto.builder()
                    .id(BenefitTestMother.DEFAULT_PURCHASE_ID)
                    .state(BenefitPurchaseState.USE_REQUESTED)
                    .build()
            );

            PaginatedData<BenefitPurchaseSimpleResponseDto> paginatedData = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                .results(purchases)
                .count(1)
                .totalPages(1)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListUseRequestedPaginatedService.cu108ListUseRequestedPaginated(
                Mockito.any(), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/use-requested/paginated")
                    .param("page", "1")
                    .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListUseRequestedPaginatedService).cu108ListUseRequestedPaginated(
                Mockito.any(), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Given teacher with no use requested purchases When listing paginated Then returns 200 with empty paginated data")
        void whenTeacherHasNoUseRequested_returnsOkWithEmptyPaginatedData() throws Exception {
            // Given
            PaginatedData<BenefitPurchaseSimpleResponseDto> paginatedData = PaginatedData.<BenefitPurchaseSimpleResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListUseRequestedPaginatedService.cu108ListUseRequestedPaginated(
                Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/use-requested/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results").isEmpty())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListUseRequestedPaginatedService).cu108ListUseRequestedPaginated(
                Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }
    }
}

