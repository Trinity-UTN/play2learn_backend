package trinity.play2learn.backend.profile.ranking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectSimpleResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardParticipantResponseDto {
    
    private Long position;

    private String name;

    private Double quantity;

    private AspectSimpleResponseDto selectedBody;

    private AspectSimpleResponseDto selectedShirt;

    private AspectSimpleResponseDto selectedHat; 

    private Long experienceLevel;
}
