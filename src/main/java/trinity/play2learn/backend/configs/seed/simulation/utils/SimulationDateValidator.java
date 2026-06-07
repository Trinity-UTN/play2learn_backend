package trinity.play2learn.backend.configs.seed.simulation.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;

/**
 * Valida coherencia temporal del simulador.
 *
 * <p>Reglas académicas R0–R8 y de inversión R9–R15.
 * Ver {@code docs/seed/simulation-investment-architecture.md}.</p>
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

    /** R9: coherencia de fechas en caja de ahorro. */
    public static void validateSavingAccountDates(LocalDate startDate, LocalDate lastUpdate) {
        if (startDate == null || lastUpdate == null) {
            throw new BadRequestException("Las fechas de caja de ahorro no pueden ser nulas");
        }
        if (lastUpdate.isBefore(startDate)) {
            throw new BadRequestException("lastUpdate no puede ser anterior a startDate (R9)");
        }
    }

    /** R10/R11: endDate exacto y estado coherente con la fecha de referencia. */
    public static void validateFixedTermDates(
        LocalDate startDate,
        LocalDate endDate,
        FixedTermDays fixedTermDays,
        FixedTermState state,
        LocalDate referenceDate
    ) {
        if (startDate == null || endDate == null || fixedTermDays == null || state == null || referenceDate == null) {
            throw new BadRequestException("Fechas de plazo fijo inválidas");
        }
        LocalDate expectedEnd = computeFixedTermEndDate(startDate, fixedTermDays);
        if (!endDate.equals(expectedEnd)) {
            throw new BadRequestException(
                "endDate debe ser startDate + " + fixedTermDays.getValor() + " días (R10)"
            );
        }
        if (state == FixedTermState.FINISHED && endDate.isAfter(referenceDate)) {
            throw new BadRequestException("Un plazo FINISHED no puede tener endDate posterior a hoy (R11)");
        }
    }

    /** R12: la orden no puede ser anterior a la existencia de la acción. */
    public static void validateOrderDate(LocalDateTime orderCreatedAt, LocalDateTime stockCreatedAt) {
        if (orderCreatedAt == null || stockCreatedAt == null) {
            throw new BadRequestException("Fechas de orden inválidas");
        }
        if (orderCreatedAt.isBefore(stockCreatedAt)) {
            throw new BadRequestException("order.createdAt no puede ser anterior a la creación del stock (R12)");
        }
    }

    /** R13: historial diario estrictamente creciente. */
    public static void validateStockHistorySequence(
        LocalDateTime previousCreatedAt,
        LocalDateTime currentCreatedAt
    ) {
        if (previousCreatedAt == null || currentCreatedAt == null) {
            throw new BadRequestException("Fechas de historial de stock inválidas");
        }
        if (!currentCreatedAt.isAfter(previousCreatedAt)) {
            throw new BadRequestException("stockHistory.createdAt debe ser estrictamente creciente (R13)");
        }
    }

    public static LocalDate computeFixedTermEndDate(LocalDate startDate, FixedTermDays fixedTermDays) {
        return startDate.plusDays(fixedTermDays.getValor());
    }
}
