package trinity.play2learn.backend.statistics.investment.mappers;

import trinity.play2learn.backend.statistics.investment.dtos.response.FixedTermDepositStatisticsResponseDto;

public class FixedTermDepositStatisticsMapper {

    public static FixedTermDepositStatisticsResponseDto toDto (
        Double totalInvested,
        Double totalReward,
        Integer quantityInProgress
    ){
        return FixedTermDepositStatisticsResponseDto.builder()
            .totalInvested(totalInvested)
            .totalReward(totalReward)
            .quantityInProgress(quantityInProgress)
            .build();
    }
    
}
