package trinity.play2learn.backend.configs.seed.simulation.utils;

import java.time.LocalDateTime;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;

/**
 * Valida coherencia temporal del simulador.
 *
 * <p>Reglas: from &lt;= to; cada evento posterior no puede preceder a su causa
 * (created &lt;= start &lt;= attempt.start &lt;= attempt.complete).</p>
 */
public final class SimulationDateValidator {

    private SimulationDateValidator() {
    }

    public static void validateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new BadRequestException("fromDate y toDate son obligatorios");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("fromDate no puede ser posterior a toDate");
        }
    }

    public static void validateActivityDates(
        LocalDateTime createdAt,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        if (createdAt == null || startDate == null || endDate == null) {
            throw new BadRequestException("Las fechas de actividad no pueden ser nulas");
        }
        if (startDate.isBefore(createdAt)) {
            throw new BadRequestException("startDate no puede ser anterior a createdAt");
        }
        if (!endDate.isAfter(startDate)) {
            throw new BadRequestException("endDate debe ser posterior a startDate");
        }
    }

    public static void validateAttemptDates(
        LocalDateTime activityStart,
        LocalDateTime startedAt,
        LocalDateTime completedAt
    ) {
        if (activityStart == null || startedAt == null || completedAt == null) {
            throw new BadRequestException("Las fechas de intento no pueden ser nulas");
        }
        if (startedAt.isBefore(activityStart)) {
            throw new BadRequestException("startedAt no puede ser anterior al startDate de la actividad");
        }
        if (completedAt.isBefore(startedAt)) {
            throw new BadRequestException("completedAt no puede ser anterior a startedAt");
        }
    }

    public static void validateBenefitLifecycle(
        LocalDateTime benefitCreatedAt,
        LocalDateTime endAt,
        LocalDateTime purchasedAt,
        LocalDateTime usedAt
    ) {
        if (benefitCreatedAt == null || endAt == null) {
            throw new BadRequestException("Fechas de beneficio inválidas");
        }
        if (!endAt.isAfter(benefitCreatedAt)) {
            throw new BadRequestException("endAt del beneficio debe ser posterior a su creación");
        }
        if (purchasedAt != null && purchasedAt.isBefore(benefitCreatedAt)) {
            throw new BadRequestException("purchasedAt no puede ser anterior a la creación del beneficio");
        }
        if (usedAt != null && purchasedAt != null && usedAt.isBefore(purchasedAt)) {
            throw new BadRequestException("usedAt no puede ser anterior a purchasedAt");
        }
    }
}
