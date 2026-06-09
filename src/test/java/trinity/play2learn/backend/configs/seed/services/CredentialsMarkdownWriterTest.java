package trinity.play2learn.backend.configs.seed.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.configs.seed.config.DatabaseSeedProperties;
import trinity.play2learn.backend.configs.seed.dtos.SeedCountsDto;
import trinity.play2learn.backend.configs.seed.dtos.SeedCredentialsDto;
import trinity.play2learn.backend.user.models.Role;

@ExtendWith(MockitoExtension.class)
class CredentialsMarkdownWriterTest {

    @Mock
    private DatabaseSeedProperties properties;

    @InjectMocks
    private CredentialsMarkdownWriter writer;

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Given credentials When write Then generates markdown file with tables")
    void whenWrite_generatesMarkdownWithTables() throws IOException {
        Path output = tempDir.resolve("credentials.md");
        when(properties.getCredentialsOutputPath()).thenReturn(output.toString());

        List<SeedCredentialsDto> credentials = List.of(
            SeedCredentialsDto.builder()
                .email("dev@gmail.com")
                .password("12345678")
                .role(Role.ROLE_DEV.name())
                .build(),
            SeedCredentialsDto.builder()
                .email("teacher@test.com")
                .password("30123456")
                .role(Role.ROLE_TEACHER.name())
                .dni("30123456")
                .name("Ana García")
                .build()
        );

        String path = writer.write(credentials, SeedCountsDto.builder().teachers(1).build());

        assertThat(path).isEqualTo(output.toAbsolutePath().normalize().toString());
        String content = Files.readString(output);
        assertThat(content).contains("dev@gmail.com", "12345678", "Docentes", "30123456");
    }
}
