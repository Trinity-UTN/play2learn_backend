package trinity.play2learn.backend.configs.seed.simulation.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResultDto {

    private String message;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private SimulationCountsDto counts;
}
