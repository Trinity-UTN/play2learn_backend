package trinity.play2learn.backend.statistics.home.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StatisticsHomeTeacherResponseDto {

    public int totalStudents;

    public int totalActivities;

    public int totalCourses;

    public int totalBenefits;

    public List<StatisticsSubjectDataDto> subjectsStatistics;

    public List<StatisticsActivityDataDto> activitiesStatistics;
    
    
}
