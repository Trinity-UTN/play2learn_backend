package trinity.play2learn.backend.statistics.home.mappers;

import java.util.List;

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityRealizationResponseDto;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeStudentResponseDto;

public class StatisticsHomeStudentMapper {

    public static StatisticsHomeStudentResponseDto toDto (
        int totalPoints, 
        int positionCourseRanking, 
        int totalActivitiesAvailable,
        int totalBenefitsAvailable,
        List<StatisticsActivityRealizationResponseDto> lastRealizations
    ) {
        return StatisticsHomeStudentResponseDto.builder()
            .totalPoints(totalPoints)
            .positionCourseRanking(positionCourseRanking)
            .totalActivitiesAvailable(totalActivitiesAvailable)
            .totalBenefitsAvailable(totalBenefitsAvailable)
            .lastRealizations(lastRealizations)
            .build();

    }
    
}
