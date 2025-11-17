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
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByTeacherPaginatedService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.configs.response.PaginatedData;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListPaginatedController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListPaginatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListByTeacherPaginatedService benefitListByTeacherPaginatedService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/teacher/paginated")
    class ListByTeacherPaginated {

        @Test
        @DisplayName("Given teacher with benefits When listing paginated Then returns 200 with paginated data")
        void whenTeacherHasBenefits_returnsOkWithPaginatedData() throws Exception {
            // Given
            List<BenefitResponseDto> benefits = Arrays.asList(
                BenefitTestMother.benefitResponseBuilder(BenefitTestMother.DEFAULT_BENEFIT_ID)
                    .build()
            );

            PaginatedData<BenefitResponseDto> paginatedData = PaginatedData.<BenefitResponseDto>builder()
                .results(benefits)
                .count(1)
                .totalPages(1)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListByTeacherPaginatedService.cu56ListBenefitsPaginated(
                Mockito.any(), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/paginated")
                    .param("page", "1")
                    .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListByTeacherPaginatedService).cu56ListBenefitsPaginated(
                Mockito.any(), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Given teacher with no benefits When listing paginated Then returns 200 with empty paginated data")
        void whenTeacherHasNoBenefits_returnsOkWithEmptyPaginatedData() throws Exception {
            // Given
            PaginatedData<BenefitResponseDto> paginatedData = PaginatedData.<BenefitResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListByTeacherPaginatedService.cu56ListBenefitsPaginated(
                Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results").isEmpty())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListByTeacherPaginatedService).cu56ListBenefitsPaginated(
                Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }
    }
}

