package trinity.play2learn.backend.configs.seed.simulation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.logs.services.LogsClientService;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationCountsDto;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationRequestDto;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationResultDto;
import trinity.play2learn.backend.configs.seed.simulation.services.interfaces.IDatabaseSimulationService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SimulationSeedControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IDatabaseSimulationService databaseSimulationService;

    @Mock
    private LogsClientService logsClientService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SimulationSeedController(databaseSimulationService))
            .setControllerAdvice(new GlobalExceptionHandler(logsClientService))
            .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /api/dev/seed/simulate When valid request Then returns 201")
    void simulateWhenValid_returnsCreated() throws Exception {
        SimulationRequestDto request = SimulationRequestDto.builder()
            .fromDate(LocalDateTime.of(2025, 1, 1, 8, 0))
            .toDate(LocalDateTime.of(2025, 3, 31, 18, 0))
            .build();

        when(databaseSimulationService.execute(any())).thenReturn(
            SimulationResultDto.builder()
                .message("ok")
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .counts(SimulationCountsDto.builder().activities(10).build())
                .build()
        );

        mockMvc.perform(post("/api/dev/seed/simulate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.message").value("ok"));

        verify(databaseSimulationService).execute(any());
    }
}
