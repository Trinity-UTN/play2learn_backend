package trinity.play2learn.backend.configs.seed.simulation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuración del simulador de actividad académica (solo desarrollo).
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
}
