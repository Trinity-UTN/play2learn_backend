package trinity.play2learn.backend.statistics.home.mappers;

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsSubjectDataDto;

public class StatisticsSubjectDataMapper {

    public static final StatisticsSubjectDataDto toDto (Long id, String name, Integer totalStudents, Integer totalActivities) {
        return StatisticsSubjectDataDto.builder()
            .id(id)
            .name(name)
            .totalStudents(totalStudents)
            .totalActivities(totalActivities)
            .build();
    }

    
    
}
