package trinity.play2learn.backend.statistics.investment.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FixedTermDepositStatisticsResponseDto {
    
    private Double totalInvested;

    private Double totalReward;

    private Integer quantityInProgress;

}
