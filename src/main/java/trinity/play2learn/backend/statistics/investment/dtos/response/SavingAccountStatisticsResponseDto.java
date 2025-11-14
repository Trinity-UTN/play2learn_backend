package trinity.play2learn.backend.statistics.investment.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavingAccountStatisticsResponseDto {
    
    private Double totalInvested;

    private Integer quantityInProgress;

    private Double totalReward;
    
}
