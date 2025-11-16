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
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByStudentPaginatedService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.configs.response.PaginatedData;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListByStudentPaginatedController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListByStudentPaginatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListByStudentPaginatedService benefitListByStudentPaginatedService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/student/paginated")
    class ListByStudentPaginated {

        @Test
        @DisplayName("Given student with benefits When listing paginated Then returns 200 with paginated data")
        void whenStudentHasBenefits_returnsOkWithPaginatedData() throws Exception {
            // Given
            List<BenefitStudentResponseDto> benefits = Arrays.asList(
                BenefitStudentResponseDto.builder()
                    .id(BenefitTestMother.DEFAULT_BENEFIT_ID)
                    .state(BenefitStudentState.AVAILABLE)
                    .build()
            );

            PaginatedData<BenefitStudentResponseDto> paginatedData = PaginatedData.<BenefitStudentResponseDto>builder()
                .results(benefits)
                .count(1)
                .totalPages(1)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListByStudentPaginatedService.cu80ListBenefitsByStudentPaginated(
                Mockito.any(), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/student/paginated")
                    .param("page", "1")
                    .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListByStudentPaginatedService).cu80ListBenefitsByStudentPaginated(
                Mockito.any(), Mockito.eq(1), Mockito.eq(10), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Given student with no benefits When listing paginated Then returns 200 with empty paginated data")
        void whenStudentHasNoBenefits_returnsOkWithEmptyPaginatedData() throws Exception {
            // Given
            PaginatedData<BenefitStudentResponseDto> paginatedData = PaginatedData.<BenefitStudentResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(benefitListByStudentPaginatedService.cu80ListBenefitsByStudentPaginated(
                Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then
            mockMvc.perform(get("/benefits/student/paginated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results").isEmpty())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListByStudentPaginatedService).cu80ListBenefitsByStudentPaginated(
                Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                Mockito.anyString(), Mockito.anyString(), 
                Mockito.any(), Mockito.any(), Mockito.any());
        }
    }
}

