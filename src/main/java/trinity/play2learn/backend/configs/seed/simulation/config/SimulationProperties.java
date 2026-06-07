package trinity.play2learn.backend.configs.seed.simulation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuración del simulador de actividad académica e inversiones (solo desarrollo).
 *
 * @see docs/seed/simulation-investment-architecture.md
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.seed.simulation")
public class SimulationProperties {

    private long randomSeed = 42L;

    private int minActivitiesPerSubject = 2;

    private int maxActivitiesPerSubject = 4;

    private int minBenefitsPerSubject = 1;

    private int maxBenefitsPerSubject = 3;

    private int maxActivitiesTotal = 200;

    private int maxBenefitsTotal = 150;

    private double studentsPerActivityRate = 0.70;

    private double studentsBuyingBenefitRate = 0.40;

    private double purchasersRequestingUseRate = 0.65;

    private double requestsAcceptedRate = 0.85;

    private double studentsBuyingAspectRate = 0.25;

    private double passOnAttempt1Rate = 0.35;

    private double passOnAttempt2Rate = 0.30;

    private double passOnAttempt3Rate = 0.20;

    private double failAllAttemptsRate = 0.15;

    private int activityInitialBalanceMin = 80;

    private int activityInitialBalanceMax = 200;

    private int benefitCostMin = 50;

    private int benefitCostMax = 400;

    private int minMinutesBetweenEvents = 5;

    private int maxMinutesBetweenEvents = 120;

    // --- Inversiones (fases 6–8) ---

    /** Tasa de estudiantes que abren caja de ahorro simulada. */
    private double studentsUsingSavingAccountRate = 0.35;

    /** Tasa de estudiantes que invierten en plazo fijo. */
    private double studentsUsingFixedTermRate = 0.30;

    /** Tasa de estudiantes que operan acciones. */
    private double studentsTradingStocksRate = 0.40;

    /** Tasa de compradores que venden al menos parcialmente. */
    private double stockSellAfterBuyRate = 0.45;

    private int minStocksToCreate = 5;

    private int maxStocksToCreate = 12;

    private double stockInitialPriceMin = 10.0;

    /** Factor sobre P25 de saldo para calcular precio inicial máximo. */
    private double stockInitialPriceMaxBalanceFactor = 0.15;

    private int savingAccountHoldingDaysMin = 7;

    private int savingAccountHoldingDaysMax = 15;

    /** Proporción de plazos fijos con endDate <= hoy (FINISHED). */
    private double fixedTermFinishedBeforeTodayRate = 0.75;

    private int minInvestmentAmount = 50;

    /** Máximo % del balance invertible por operación. */
    private double maxInvestmentAmountFactorOfBalance = 0.40;

    private int minStockBuyQuantity = 1;

    private int maxStockBuyQuantity = 10;

    private int maxStockTradesPerStudent = 3;

    private int maxInvestmentsPerStudent = 1;
}
