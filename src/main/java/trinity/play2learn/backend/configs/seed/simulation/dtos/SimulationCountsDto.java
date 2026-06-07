package trinity.play2learn.backend.configs.seed.simulation.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationCountsDto {

    private int activities;
    private int attempts;
    private int approved;
    private int disapproved;
    private int benefits;
    private int benefitPurchases;
    private int benefitUseRequests;
    private int benefitUsesAccepted;
    private int aspectPurchases;
}
