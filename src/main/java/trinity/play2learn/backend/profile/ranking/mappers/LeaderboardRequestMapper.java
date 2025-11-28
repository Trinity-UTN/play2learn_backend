package trinity.play2learn.backend.profile.ranking.mappers;

import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.models.LeaderboardType;

public class LeaderboardRequestMapper {
    
    public static LeaderboardRequestDto toDto(LeaderboardType type, Long id) {
        return LeaderboardRequestDto.builder()
            .type(type)
            .id(id) // Puede ser null para INSTITUCION
            .build();
    }
}
