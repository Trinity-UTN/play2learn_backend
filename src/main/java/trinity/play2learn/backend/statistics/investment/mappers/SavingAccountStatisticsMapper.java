package trinity.play2learn.backend.statistics.investment.mappers;

import trinity.play2learn.backend.statistics.investment.dtos.response.SavingAccountStatisticsResponseDto;

public class SavingAccountStatisticsMapper {
    
    public static SavingAccountStatisticsResponseDto toDto (
        Double totalInvested,
        Integer quantityInProgress,
        Double totalReward
    ) {
        return SavingAccountStatisticsResponseDto.builder()
            .totalInvested(totalInvested)
            .quantityInProgress(quantityInProgress)
            .totalReward(totalReward)
            .build();
    };
}
