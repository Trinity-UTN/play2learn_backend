package trinity.play2learn.backend.configs.seed.simulation.collector;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.configs.seed.simulation.dtos.SimulationCountsDto;

/**
 * Acumula entidades y contadores durante la simulación.
 */
@Getter
public class SimulationDataCollector {

    private final List<Long> activityIds = new ArrayList<>();
    private final List<Benefit> benefits = new ArrayList<>();

    private int attempts;
    private int approved;
    private int disapproved;
    private int benefitPurchases;
    private int benefitUseRequests;
    private int benefitUsesAccepted;
    private int aspectPurchases;

    public void addActivityId(Long activityId) {
        activityIds.add(activityId);
    }

    public void addBenefit(Benefit benefit) {
        benefits.add(benefit);
    }

    public void incrementAttempts() {
        attempts++;
    }

    public void incrementApproved() {
        approved++;
    }

    public void incrementDisapproved() {
        disapproved++;
    }

    public void incrementBenefitPurchases() {
        benefitPurchases++;
    }

    public void incrementBenefitUseRequests() {
        benefitUseRequests++;
    }

    public void incrementBenefitUsesAccepted() {
        benefitUsesAccepted++;
    }

    public void incrementAspectPurchases() {
        aspectPurchases++;
    }

    public SimulationCountsDto toCounts() {
        return SimulationCountsDto.builder()
            .activities(activityIds.size())
            .attempts(attempts)
            .approved(approved)
            .disapproved(disapproved)
            .benefits(benefits.size())
            .benefitPurchases(benefitPurchases)
            .benefitUseRequests(benefitUseRequests)
            .benefitUsesAccepted(benefitUsesAccepted)
            .aspectPurchases(aspectPurchases)
            .build();
    }
}
