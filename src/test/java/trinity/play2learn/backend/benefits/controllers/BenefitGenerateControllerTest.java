package trinity.play2learn.backend.benefits.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import trinity.play2learn.backend.benefits.BenefitTestMother;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGenerateService;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.user.models.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BenefitGenerateController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenefitGenerateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBenefitGenerateService benefitGenerateService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /benefits")
    class GenerateBenefit {

        @Test
        @DisplayName("Given valid request When generating benefit Then returns 201 Created with benefit response")
        void whenValidRequest_returnsCreated() throws Exception {
            // Given - Request DTO válido con todos los campos requeridos
            BenefitRequestDto requestDto = BenefitTestMother.benefitRequestBuilder(BenefitTestMother.DEFAULT_SUBJECT_ID)
                .build();

            // Mock - Servicio retorna beneficio creado
            BenefitResponseDto responseDto = BenefitTestMother.benefitResponseBuilder(BenefitTestMother.DEFAULT_BENEFIT_ID)
                .build();

            Mockito.when(benefitGenerateService.cu51GenerateBenefit(Mockito.any(BenefitRequestDto.class), Mockito.any(User.class)))
                .thenReturn(responseDto);

            // When & Then - POST /benefits debe retornar 201 con datos del beneficio
            mockMvc.perform(post("/benefits")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(BenefitTestMother.DEFAULT_BENEFIT_ID))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(benefitGenerateService).cu51GenerateBenefit(Mockito.any(BenefitRequestDto.class), Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given invalid request with null name When generating benefit Then returns 400 Bad Request")
        void whenInvalidRequestWithNullName_returnsBadRequest() throws Exception {
            // Given - Request DTO inválido: nombre es null
            BenefitRequestDto requestDto = BenefitTestMother.benefitRequestBuilder(BenefitTestMother.DEFAULT_SUBJECT_ID)
                .name(null)
                .build();

            // When & Then - Validación de Bean Validation debe rechazar la petición
            mockMvc.perform(post("/benefits")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

            // Verify - Servicio no debe ser llamado por validación fallida
            Mockito.verifyNoInteractions(benefitGenerateService);
        }

        @Test
        @DisplayName("Given invalid request with null subjectId When generating benefit Then returns 400 Bad Request")
        void whenInvalidRequestWithNullSubjectId_returnsBadRequest() throws Exception {
            // Given - Request DTO inválido: subjectId es null
            BenefitRequestDto requestDto = BenefitTestMother.benefitRequestBuilder(null)
                .build();

            // When & Then - Validación de Bean Validation debe rechazar la petición
            mockMvc.perform(post("/benefits")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

            // Verify - Servicio no debe ser llamado por validación fallida
            Mockito.verifyNoInteractions(benefitGenerateService);
        }
    }
}

