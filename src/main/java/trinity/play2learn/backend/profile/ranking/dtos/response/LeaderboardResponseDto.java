package trinity.play2learn.backend.profile.ranking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponseDto {
    
    private LeaderboardParticipantResponseDto currentUserPosition;

    private List<LeaderboardParticipantResponseDto> participants;
    
}
