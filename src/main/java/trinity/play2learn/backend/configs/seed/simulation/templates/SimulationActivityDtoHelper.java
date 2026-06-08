package trinity.play2learn.backend.configs.seed.simulation.templates;

import java.time.LocalDateTime;
import java.util.List;

import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;

/**
 * Helpers para construir DTOs de actividad sin depender de builders inconsistentes.
 */
final class SimulationActivityDtoHelper {

    private SimulationActivityDtoHelper() {
    }

    static void fillBase(
        trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto dto,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Difficulty difficulty,
        int maxTime,
        int attempts,
        double initialBalance
    ) {
        dto.setDescription(description);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setDifficulty(difficulty);
        dto.setMaxTime(maxTime);
        dto.setAttempts(attempts);
        dto.setInitialBalance(initialBalance);
    }
}
