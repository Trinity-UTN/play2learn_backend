package trinity.play2learn.backend.profile.ranking.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.profile.ranking.models.LeaderboardType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardRequestDto {

    private LeaderboardType type;

    private Long id;
    
}
