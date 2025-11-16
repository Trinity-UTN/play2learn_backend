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
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByTeacherService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitListController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitListByTeacherService benefitListByTeacherService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/teacher")
    class ListByTeacher {

        @Test
        @DisplayName("Given teacher with benefits When listing benefits Then returns 200 OK with benefits list")
        void whenTeacherHasBenefits_returnsOkWithList() throws Exception {
            // Given - Mock de lista de beneficios del profesor
            List<BenefitResponseDto> benefits = Arrays.asList(
                BenefitTestMother.benefitResponseBuilder(BenefitTestMother.DEFAULT_BENEFIT_ID)
                    .build()
            );

            Mockito.when(benefitListByTeacherService.cu55ListBenefitsByTeacher(Mockito.any()))
                .thenReturn(benefits);

            // When & Then - GET /benefits/teacher debe retornar 200 con array de beneficios
            mockMvc.perform(get("/benefits/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(benefitListByTeacherService).cu55ListBenefitsByTeacher(Mockito.any());
        }

        @Test
        @DisplayName("Given teacher with no benefits When listing benefits Then returns 200 OK with empty list")
        void whenTeacherHasNoBenefits_returnsOkWithEmptyList() throws Exception {
            // Given - Mock de lista vacía cuando el profesor no tiene beneficios
            List<BenefitResponseDto> emptyList = List.of();

            Mockito.when(benefitListByTeacherService.cu55ListBenefitsByTeacher(Mockito.any()))
                .thenReturn(emptyList);

            // When & Then - GET /benefits/teacher debe retornar 200 con array vacío
            mockMvc.perform(get("/benefits/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(benefitListByTeacherService).cu55ListBenefitsByTeacher(Mockito.any());
        }
    }
}

