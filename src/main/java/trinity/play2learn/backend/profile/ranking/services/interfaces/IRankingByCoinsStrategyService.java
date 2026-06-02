package trinity.play2learn.backend.profile.ranking.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;

public interface IRankingByCoinsStrategyService {
    
    LeaderboardResponseDto execute(LeaderboardRequestDto leaderboardRequestDto, Student student);

}
