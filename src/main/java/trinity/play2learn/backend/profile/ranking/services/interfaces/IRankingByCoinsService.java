package trinity.play2learn.backend.profile.ranking.services.interfaces;

import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IRankingByCoinsService {
    
    LeaderboardResponseDto cu120rankingByCoins (LeaderboardRequestDto leaderboardRequestDto, User user);

}
