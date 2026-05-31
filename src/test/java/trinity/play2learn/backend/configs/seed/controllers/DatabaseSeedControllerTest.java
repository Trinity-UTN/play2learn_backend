package trinity.play2learn.backend.configs.seed.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import trinity.play2learn.backend.configs.logs.services.LogsClientService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import trinity.play2learn.backend.configs.exceptions.GlobalExceptionHandler;
import trinity.play2learn.backend.configs.seed.dtos.SeedCountsDto;
import trinity.play2learn.backend.configs.seed.dtos.SeedResultDto;
import trinity.play2learn.backend.configs.seed.services.interfaces.IDatabaseSeedService;
import trinity.play2learn.backend.user.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DatabaseSeedControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IDatabaseSeedService databaseSeedService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private LogsClientService logsClientService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DatabaseSeedController(databaseSeedService, userRepository))
            .setControllerAdvice(new GlobalExceptionHandler(logsClientService))
            .build();
    }

    @Test
    @DisplayName("POST /api/dev/seed/bootstrap When empty DB Then returns 201")
    void bootstrapWhenEmptyDb_returnsCreated() throws Exception {
        when(userRepository.count()).thenReturn(0L);
        when(databaseSeedService.execute()).thenReturn(
            SeedResultDto.builder()
                .message("ok")
                .counts(SeedCountsDto.builder().years(6).build())
                .credentialsFilePath("docs/seed/credentials.md")
                .build()
        );

        mockMvc.perform(post("/api/dev/seed/bootstrap"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.message").value("ok"));

        verify(databaseSeedService).execute();
    }

    @Test
    @DisplayName("POST /api/dev/seed/bootstrap When users exist Then returns 401")
    void bootstrapWhenUsersExist_returnsUnauthorized() throws Exception {
        when(userRepository.count()).thenReturn(1L);

        mockMvc.perform(post("/api/dev/seed/bootstrap"))
            .andExpect(status().isUnauthorized());
    }
}
