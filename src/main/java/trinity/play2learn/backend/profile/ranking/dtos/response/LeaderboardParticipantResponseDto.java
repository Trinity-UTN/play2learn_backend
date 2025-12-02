package trinity.play2learn.backend.profile.ranking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardParticipantResponseDto {
    
    private Long position;

    private String name;

    private Double quantity;

}
