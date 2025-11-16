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
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchasedUsedResponseDto;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUsedByStudentService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListUsedByStudentController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListUsedByStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListUsedByStudentService benefitListUsedByStudentService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/student/used")
    class ListUsedByStudent {

        @Test
        @DisplayName("Given student with used purchases When listing used purchases Then returns 200 with list")
        void whenStudentHasUsedPurchases_returnsOkWithList() throws Exception {
            // Given
            List<BenefitPurchasedUsedResponseDto> purchases = Arrays.asList(
                BenefitPurchasedUsedResponseDto.builder()
                    .id(BenefitTestMother.DEFAULT_PURCHASE_ID)
                    .state(BenefitPurchaseState.USED)
                    .build()
            );

            Mockito.when(benefitListUsedByStudentService.cu93ListUsedByStudent(Mockito.any()))
                .thenReturn(purchases);

            // When & Then
            mockMvc.perform(get("/benefits/student/used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(BenefitTestMother.DEFAULT_PURCHASE_ID))
                .andExpect(jsonPath("$.data[0].state").value(BenefitPurchaseState.USED.name()))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListUsedByStudentService).cu93ListUsedByStudent(Mockito.any());
        }

        @Test
        @DisplayName("Given student with no used purchases When listing used purchases Then returns 200 with empty list")
        void whenStudentHasNoUsedPurchases_returnsOkWithEmptyList() throws Exception {
            // Given
            List<BenefitPurchasedUsedResponseDto> emptyList = List.of();

            Mockito.when(benefitListUsedByStudentService.cu93ListUsedByStudent(Mockito.any()))
                .thenReturn(emptyList);

            // When & Then
            mockMvc.perform(get("/benefits/student/used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitListUsedByStudentService).cu93ListUsedByStudent(Mockito.any());
        }
    }
}

