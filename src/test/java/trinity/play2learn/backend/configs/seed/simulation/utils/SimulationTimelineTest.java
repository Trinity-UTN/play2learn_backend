package trinity.play2learn.backend.configs.seed.simulation.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;

class SimulationTimelineTest {

    private SimulationTimeline timeline;

    @BeforeEach
    void setUp() {
        SimulationProperties properties = new SimulationProperties();
        properties.setRandomSeed(42L);
        timeline = new SimulationTimeline(
            LocalDateTime.of(2025, 1, 1, 8, 0),
            LocalDateTime.of(2025, 3, 31, 18, 0),
            properties
        );
    }

    @Test
    @DisplayName("Given cursor at activity end When resolving attempt Then completedAt is not before startedAt")
    void resolveAttemptWindow_cursorAtEnd_producesCoherentDates() {
        LocalDateTime activityStart = LocalDateTime.of(2025, 1, 10, 9, 0);
        LocalDateTime activityEnd = LocalDateTime.of(2025, 1, 10, 18, 0);

        SimulationTimeline.AttemptWindow window = timeline.resolveAttemptWindow(
            activityStart, activityEnd, activityEnd
        );

        assertThat(window.startedAt()).isBeforeOrEqualTo(window.completedAt());
        assertThat(window.startedAt()).isAfterOrEqualTo(activityStart);
        assertThat(window.completedAt()).isBeforeOrEqualTo(activityEnd);
        SimulationDateValidator.validateAttemptDates(activityStart, window.startedAt(), window.completedAt());
    }
}
