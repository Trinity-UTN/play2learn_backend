package trinity.play2learn.backend.configs.seed.simulation.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;

class SimulationDateValidatorTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2025, 1, 1, 8, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2025, 3, 31, 18, 0);

    @Nested
    @DisplayName("validateRange")
    class ValidateRange {

        @Test
        @DisplayName("Given valid range When validating Then passes")
        void validRange_passes() {
            SimulationDateValidator.validateRange(FROM, TO);
        }

        @Test
        @DisplayName("Given from after to When validating Then throws")
        void fromAfterTo_throws() {
            assertThatThrownBy(() -> SimulationDateValidator.validateRange(TO, FROM))
                .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("Given equal dates When validating Then passes")
        void equalDates_passes() {
            SimulationDateValidator.validateRange(FROM, FROM);
        }
    }

    @Nested
    @DisplayName("validateAttemptDates")
    class ValidateAttemptDates {

        @Test
        @DisplayName("Given coherent attempt dates When validating Then passes")
        void coherentAttempt_passes() {
            LocalDateTime start = FROM.plusDays(1);
            LocalDateTime started = start.plusHours(1);
            LocalDateTime completed = started.plusMinutes(30);
            SimulationDateValidator.validateAttemptDates(start, started, completed);
        }

        @Test
        @DisplayName("Given started before activity start When validating Then throws")
        void startedBeforeActivityStart_throws() {
            LocalDateTime activityStart = FROM.plusDays(2);
            LocalDateTime started = FROM.plusDays(1);
            assertThatThrownBy(() ->
                SimulationDateValidator.validateAttemptDates(activityStart, started, started.plusMinutes(10))
            ).isInstanceOf(BadRequestException.class);
        }
    }

    @Test
    @DisplayName("validateActivityDates rejects end before start")
    void activityDates_invalidOrder() {
        LocalDateTime created = FROM;
        LocalDateTime start = FROM.plusDays(1);
        LocalDateTime end = start.minusHours(1);
        assertThatThrownBy(() -> SimulationDateValidator.validateActivityDates(created, start, end))
            .isInstanceOf(BadRequestException.class);
    }
}
