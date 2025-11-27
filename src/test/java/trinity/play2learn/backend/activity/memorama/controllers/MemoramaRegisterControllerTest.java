package trinity.play2learn.backend.activity.memorama.controllers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.memorama.MemoramaTestMother;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.services.interfaces.IMemoramaGenerateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class MemoramaRegisterControllerTest {

    private static final String BASE_PATH = "/activities/memorama";

    private MockMvc mockMvc;

    @Mock
    private IMemoramaGenerateService memoramaRegisterService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new MemoramaRegisterController(memoramaRegisterService)
            )
            .setControllerAdvice(new GlobalExceptionHandler(null))
            .build();

        reset(memoramaRegisterService);
    }

    @Nested
    @DisplayName("POST /activities/memorama")
    class RegisterMemorama {

        @Test
        @DisplayName("Given valid request with 4 couples and images When registering memorama Then returns MemoramaResponseDto with 201 Created")
        void shouldRegisterMemoramaSuccessfully() throws Exception {
            // Given
            MemoramaResponseDto responseDto = MemoramaTestMother.validMemoramaResponseDto(
                ActivityTestMother.ACTIVITY_ID
            );
            LocalDateTime startDate = LocalDateTime.now().plusDays(1);
            LocalDateTime endDate = LocalDateTime.now().plusDays(7);

            when(memoramaRegisterService.cu41GenerateMemorama(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            )).thenReturn(responseDto);

            // When & Then
            performPost(startDate, endDate, List.of("Perro", "Gato", "Pájaro", "Peces"), 
                List.of(
                    MemoramaTestMother.mockImage("image1.jpg"),
                    MemoramaTestMother.mockImage("image2.jpg"),
                    MemoramaTestMother.mockImage("image3.jpg"),
                    MemoramaTestMother.mockImage("image4.jpg")
                ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.name").value("Memorama"))
                .andExpect(jsonPath("$.data.couples").isArray())
                .andExpect(jsonPath("$.message").value(SuccessfulMessages.createdSuccessfully("Actividad de Memorama")));

            verify(memoramaRegisterService).cu41GenerateMemorama(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given teacher not assigned to subject When registering memorama Then returns 409 Conflict")
        void shouldReturnConflictWhenTeacherNotAssigned() throws Exception {
            // Given
            LocalDateTime startDate = LocalDateTime.now().plusDays(1);
            LocalDateTime endDate = LocalDateTime.now().plusDays(7);

            // Mock the mapper to return a valid DTO (bypass validation)
            // The mapper validates before service is called, so we need to ensure
            // the DTO passes validation to reach the service where ConflictException is thrown
            when(memoramaRegisterService.cu41GenerateMemorama(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            )).thenThrow(new ConflictException("El docente no esta asignado a la materia"));

            // When & Then
            // Use valid couples(4) with matching images to pass mapper validation
            // Send images directly as list like the successful test
            performPost(startDate, endDate, List.of("Perro", "Gato", "Pájaro", "Peces"), 
                List.of(
                    MemoramaTestMother.mockImage("image1.jpg"),
                    MemoramaTestMother.mockImage("image2.jpg"),
                    MemoramaTestMother.mockImage("image3.jpg"),
                    MemoramaTestMother.mockImage("image4.jpg")
                ))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El docente no esta asignado a la materia"))
                .andExpect(jsonPath("$.errors[0]").value("El docente no esta asignado a la materia"));

            verify(memoramaRegisterService).cu41GenerateMemorama(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            );
        }

        @Test
        @DisplayName("Given invalid couples size (parejas incompletas - edge case) When registering memorama Then returns 400 BadRequest")
        void shouldReturnBadRequestWhenInvalidCouplesSize() throws Exception {
            // Given
            LocalDateTime startDate = LocalDateTime.now().plusDays(1);
            LocalDateTime endDate = LocalDateTime.now().plusDays(7);

            // When & Then
            // Use couples(3) to trigger BadRequestException for size < 4
            // The mapper validates before service is called, so BadRequestException comes from mapper
            performPost(startDate, endDate, List.of("Perro", "Gato", "Pájaro"), 
                MemoramaTestMother.couples(3).stream()
                    .map(c -> MemoramaTestMother.mockImage())
                    .toList())
                .andExpect(status().isBadRequest());

            // Service is not called because mapper throws exception first
            verify(memoramaRegisterService, never()).cu41GenerateMemorama(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(User.class)
            );
        }
    }

    private ResultActions performPost(LocalDateTime startDate, LocalDateTime endDate, List<String> concepts, List<MockMultipartFile> images) throws Exception {
        org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder requestBuilder = multipart(BASE_PATH);
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("description", "Descripción de la actividad de memorama");
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("startDate", startDate.toString());
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("endDate", endDate.toString());
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("difficulty", Difficulty.FACIL.toString());
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("maxTime", "30");
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("subjectId", ActivityTestMother.SUBJECT_ID.toString());
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("attempts", "3");
        requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("initialBalance", "100.0");
        
        for (String concept : concepts) {
            requestBuilder = (org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder) requestBuilder.param("concepts", concept);
        }
        
        for (MockMultipartFile image : images) {
            requestBuilder = requestBuilder.file(image);
        }
        
        return mockMvc.perform(requestBuilder);
    }

}

