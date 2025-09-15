package trinity.play2learn.backend.statistics.home.mappers;

import java.util.List;

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityDataDto;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeTeacherResponseDto;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsSubjectDataDto;

public class StatisticsHomeTeacherMapper {

    public static final StatisticsHomeTeacherResponseDto toDto(
        int totalStudents, 
        int totalActivities, 
        int totalCourses, 
        int totalBenefits, 
        List<StatisticsSubjectDataDto> subjectsStatistics,
        List<StatisticsActivityDataDto> activitiesStatistics) {
        return StatisticsHomeTeacherResponseDto.builder()
            .totalStudents(totalStudents)
            .totalActivities(totalActivities)
            .totalCourses(totalCourses)
            .totalBenefits(totalBenefits)
            .subjectsStatistics(subjectsStatistics)
            .activitiesStatistics(activitiesStatistics)
            .build();
    }
    
}
