package trinity.play2learn.backend.configs.seed.simulation.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationRequestDto {

    @NotNull(message = ValidationMessages.NOT_NULL_START_DATE)
    private LocalDateTime fromDate;

    @NotNull(message = ValidationMessages.NOT_NULL_END_DATE)
    private LocalDateTime toDate;
}
