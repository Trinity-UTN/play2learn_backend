package trinity.play2learn.backend.statistics.home.mappers;

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityDataDto;

public class StatisticsActivityDataMapper {

    public static final StatisticsActivityDataDto toDto (String name, int totalRealizations, int createdDaysAgo) {
        return StatisticsActivityDataDto.builder()
            .name(name)
            .totalRealizations(totalRealizations)
            .createdDaysAgo(createdDaysAgo)
            .build();
    }
    
}
