package trinity.play2learn.backend.configs.seed.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeedCountsDto {

    @Builder.Default
    private int reserves = 0;

    @Builder.Default
    private int years = 0;

    @Builder.Default
    private int courses = 0;

    @Builder.Default
    private int teachers = 0;

    @Builder.Default
    private int students = 0;

    @Builder.Default
    private int subjects = 0;

    @Builder.Default
    private int aspects = 0;
}
