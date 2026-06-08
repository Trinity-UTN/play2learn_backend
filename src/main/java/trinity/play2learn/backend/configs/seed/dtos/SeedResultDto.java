package trinity.play2learn.backend.configs.seed.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeedResultDto {

    private String message;

    @Builder.Default
    private List<SeedCredentialsDto> credentials = new ArrayList<>();

    private SeedCountsDto counts;

    private String credentialsFilePath;
}
