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

import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentCountResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitStudentCountService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitStudentCountController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitStudentCountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitStudentCountService benefitStudentCountService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /benefits/student/count")
    class CountByStudentState {

        @Test
        @DisplayName("Given student When counting benefits by state Then returns 200 with count response")
        void whenValidRequest_returnsOkWithCount() throws Exception {
            // Given
            BenefitStudentCountResponseDto countDto = BenefitStudentCountResponseDto.builder()
                .available(5)
                .purchased(3)
                .use_requested(2)
                .used(1)
                .expired(0)
                .build();

            Mockito.when(benefitStudentCountService.cu89CountByStudentState(Mockito.any()))
                .thenReturn(countDto);

            // When & Then
            mockMvc.perform(get("/benefits/student/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(5))
                .andExpect(jsonPath("$.data.purchased").value(3))
                .andExpect(jsonPath("$.data.use_requested").value(2))
                .andExpect(jsonPath("$.data.used").value(1))
                .andExpect(jsonPath("$.data.expired").value(0))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitStudentCountService).cu89CountByStudentState(Mockito.any());
        }

        @Test
        @DisplayName("Given student with no benefits When counting benefits Then returns 200 with zeros")
        void whenNoBenefits_returnsOkWithZeros() throws Exception {
            // Given
            BenefitStudentCountResponseDto countDto = BenefitStudentCountResponseDto.builder()
                .available(0)
                .purchased(0)
                .use_requested(0)
                .used(0)
                .expired(0)
                .build();

            Mockito.when(benefitStudentCountService.cu89CountByStudentState(Mockito.any()))
                .thenReturn(countDto);

            // When & Then
            mockMvc.perform(get("/benefits/student/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(0))
                .andExpect(jsonPath("$.data.purchased").value(0))
                .andExpect(jsonPath("$.message").exists());

            Mockito.verify(benefitStudentCountService).cu89CountByStudentState(Mockito.any());
        }
    }
}

