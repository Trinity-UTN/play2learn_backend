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
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUseRequestedService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListUseRequestedByTeacherController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListUseRequestedByTeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListUseRequestedService benefitListUseRequestedService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/teacher/use-requested")
    class ListUseRequested {

        @Test
        @DisplayName("Given teacher with use requested purchases When listing use requested Then returns 200 with list")
        void whenTeacherHasUseRequested_returnsOkWithList() throws Exception {
            // Given
            List<BenefitPurchaseSimpleResponseDto> purchases = Arrays.asList(
                BenefitPurchaseSimpleResponseDto.builder()
                    .id(BenefitTestMother.DEFAULT_PURCHASE_ID)
                    .state(BenefitPurchaseState.USE_REQUESTED)
                    .build()
            );

            Mockito.when(benefitListUseRequestedService.cu82ListUseRequestedByTeacher(Mockito.any()))
                .thenReturn(purchases);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/use-requested"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .andExpect(jsonPath("$.data[0].state").value(BenefitPurchaseState.USE_REQUESTED.name()))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListUseRequestedService).cu82ListUseRequestedByTeacher(Mockito.any());
        }

        @Test
        @DisplayName("Given teacher with no use requested purchases When listing use requested Then returns 200 with empty list")
        void whenTeacherHasNoUseRequested_returnsOkWithEmptyList() throws Exception {
            // Given
            List<BenefitPurchaseSimpleResponseDto> emptyList = List.of();

            Mockito.when(benefitListUseRequestedService.cu82ListUseRequestedByTeacher(Mockito.any()))
                .thenReturn(emptyList);

            // When & Then
            mockMvc.perform(get("/benefits/teacher/use-requested"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListUseRequestedService).cu82ListUseRequestedByTeacher(Mockito.any());
        }
    }
}

