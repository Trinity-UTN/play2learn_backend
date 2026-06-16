package trinity.play2learn.backend.statistics.home.mappers;

import java.time.LocalDateTime; 

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityDataDto;

public class StatisticsActivityDataMapper {

    public static final StatisticsActivityDataDto toDto (String name, int totalRealizations, int createdDaysAgo, int totalStudents, LocalDateTime startDate) {
        return StatisticsActivityDataDto.builder()
            .name(name)
            .totalRealizations(totalRealizations)
            .createdDaysAgo(createdDaysAgo)
            .totalStudents(totalStudents)
            .startDate(startDate)
            .build();
    }
    
}
