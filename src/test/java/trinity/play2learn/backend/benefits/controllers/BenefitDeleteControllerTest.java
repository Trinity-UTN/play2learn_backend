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
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitDeleteService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitDeleteController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitDeleteService benefitDeleteService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("DELETE /benefits/teacher/{id}")
    class DeleteBenefit {

        @Test
        @DisplayName("Given valid benefit ID When deleting benefit Then returns 204")
        void whenValidBenefitId_returnsNoContent() throws Exception {
            // Given
            Long benefitId = BenefitTestMother.DEFAULT_BENEFIT_ID;

            Mockito.doNothing().when(benefitDeleteService).cu94DeleteBenefit(Mockito.any(), Mockito.eq(benefitId));

            // When & Then
            mockMvc.perform(delete("/benefits/teacher/{id}", benefitId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitDeleteService).cu94DeleteBenefit(Mockito.any(), Mockito.eq(benefitId));
        }

        @Test
        @DisplayName("Given non-existent benefit ID When deleting benefit Then service throws exception")
        void whenNonExistentBenefitId_serviceThrowsException() throws Exception {
            // Given
            Long nonExistentId = 9999L;

            Mockito.doThrow(new trinity.play2learn.backend.configs.exceptions.NotFoundException("Beneficio no encontrado"))
                .when(benefitDeleteService).cu94DeleteBenefit(Mockito.any(), Mockito.eq(nonExistentId));

            // When & Then
            mockMvc.perform(delete("/benefits/teacher/{id}", nonExistentId))
                .andExpect(status().isNotFound());

            Mockito.verify(benefitDeleteService).cu94DeleteBenefit(Mockito.any(), Mockito.eq(nonExistentId));
        }
    }
}

