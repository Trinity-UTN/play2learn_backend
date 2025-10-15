package trinity.play2learn.backend.statistics.home.mappers;

import java.util.List;

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityRealizationResponseDto;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeStudentResponseDto;

public class StatisticsHomeStudentMapper {

    public static StatisticsHomeStudentResponseDto toDto (
        int totalPoints, 
        int positionRanking, 
        int totalActivities, 
        int totalCompletedActivities, 
        List<StatisticsActivityRealizationResponseDto> lastRealizations
    ) {
        return StatisticsHomeStudentResponseDto.builder()
            .totalPoints(totalPoints)
            .positionRanking(positionRanking)
            .totalActivities(totalActivities)
            .totalCompletedActivities(totalCompletedActivities)
            .lastRealizations(lastRealizations)
            .build();

    }
    
}
