package trinity.play2learn.backend.statistics.home.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StatisticsHomeStudentResponseDto {

    private int totalPoints;

    private int positionRanking;

    private int totalActivities;

    private int totalCompletedActivities;

    private List<StatisticsActivityRealizationResponseDto> lastRealizations;
    
}
