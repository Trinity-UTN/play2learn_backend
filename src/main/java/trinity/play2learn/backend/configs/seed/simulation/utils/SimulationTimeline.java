package trinity.play2learn.backend.configs.seed.simulation.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;

/**
 * Genera fechas distribuidas de forma coherente dentro de un rango simulado.
 */
@Slf4j
public final class SimulationTimeline {

    private final LocalDateTime from;
    private final LocalDateTime to;
    private final Random random;
    private final SimulationProperties properties;

    public SimulationTimeline(LocalDateTime from, LocalDateTime to, SimulationProperties properties) {
        SimulationDateValidator.validateRange(from, to);
        this.from = from;
        this.to = to;
        this.properties = properties;
        this.random = new Random(properties.getRandomSeed());
    }

    public Random random() {
        return random;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public LocalDateTime randomBetween(LocalDateTime min, LocalDateTime max) {
        LocalDateTime effectiveMin = min.isBefore(from) ? from : min;
        LocalDateTime effectiveMax = max.isAfter(to) ? to : max;
        if (!effectiveMin.isBefore(effectiveMax)) {
            return effectiveMin;
        }
        long minutes = ChronoUnit.MINUTES.between(effectiveMin, effectiveMax);
        if (minutes <= 0) {
            return effectiveMin;
        }
        return effectiveMin.plusMinutes(random.nextLong(minutes + 1));
    }

    public LocalDateTime nextAfter(LocalDateTime min, LocalDateTime max) {
        LocalDateTime candidate = min.plusMinutes(
            properties.getMinMinutesBetweenEvents()
                + random.nextInt(
                    Math.max(1, properties.getMaxMinutesBetweenEvents() - properties.getMinMinutesBetweenEvents() + 1)
                )
        );
        if (candidate.isAfter(max)) {
            return max;
        }
        return candidate;
    }

    public LocalDateTime addMinutes(LocalDateTime base, int minutes) {
        return base.plusMinutes(minutes);
    }

    public LocalDate randomLocalDateBetween(LocalDate min, LocalDate max) {
        LocalDate effectiveMin = min;
        LocalDate effectiveMax = max;
        if (effectiveMin.isBefore(from.toLocalDate())) {
            effectiveMin = from.toLocalDate();
        }
        if (effectiveMax.isAfter(to.toLocalDate())) {
            effectiveMax = to.toLocalDate();
        }
        if (effectiveMin.isAfter(effectiveMax)) {
            return effectiveMin;
        }
        long days = ChronoUnit.DAYS.between(effectiveMin, effectiveMax);
        if (days <= 0) {
            return effectiveMin;
        }
        return effectiveMin.plusDays(random.nextLong(days + 1));
    }

    public int randomIntBetween(int min, int max) {
        if (min >= max) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Calcula inicio y fin de un intento garantizando {@code startedAt <= completedAt}
     * y ambos dentro de la ventana de la actividad.
     */
    public AttemptWindow resolveAttemptWindow(
        LocalDateTime activityStart,
        LocalDateTime activityEnd,
        LocalDateTime preferredStart
    ) {
        LocalDateTime startedAt = preferredStart;
        if (startedAt.isAfter(activityEnd)) {
            startedAt = activityEnd;
        }
        if (startedAt.isBefore(activityStart)) {
            startedAt = activityStart;
        }

        int durationMinutes = Math.max(1, 5 + random.nextInt(20));
        LocalDateTime completedAt = startedAt.plusMinutes(durationMinutes);
        if (completedAt.isAfter(activityEnd)) {
            completedAt = activityEnd;
        }
        if (!completedAt.isAfter(startedAt)) {
            startedAt = completedAt.minusMinutes(1);
            if (startedAt.isBefore(activityStart)) {
                startedAt = activityStart;
                completedAt = startedAt.plusMinutes(1);
                if (completedAt.isAfter(activityEnd)) {
                    completedAt = activityEnd;
                }
            }
        }

        SimulationDateValidator.validateAttemptDates(activityStart, startedAt, completedAt);
        return new AttemptWindow(startedAt, completedAt);
    }

    public record AttemptWindow(LocalDateTime startedAt, LocalDateTime completedAt) {
    }

    public ActivitySchedule scheduleActivity(int index, int total) {
        long totalMinutes = Math.max(1, ChronoUnit.MINUTES.between(from, to));
        long slot = totalMinutes / Math.max(1, total);
        LocalDateTime createdAt = from.plusMinutes(index * slot);
        if (createdAt.isAfter(to)) {
            createdAt = to.minusHours(1);
        }
        LocalDateTime startDate = nextAfter(createdAt, to.minusHours(1));
        LocalDateTime endDate = nextAfter(startDate, to);
        if (!endDate.isAfter(startDate)) {
            endDate = startDate.plusHours(1);
        }
        SimulationDateValidator.validateActivityDates(createdAt, startDate, endDate);
        log.debug("Actividad {} programada: created={}, start={}, end={}", index, createdAt, startDate, endDate);
        return new ActivitySchedule(createdAt, startDate, endDate);
    }

    public record ActivitySchedule(LocalDateTime createdAt, LocalDateTime startDate, LocalDateTime endDate) {
    }
}
