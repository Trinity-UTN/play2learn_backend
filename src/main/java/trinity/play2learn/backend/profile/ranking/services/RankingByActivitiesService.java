package trinity.play2learn.backend.profile.ranking.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByActivitiesService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByActivitiesStrategyService;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class RankingByActivitiesService implements IRankingByActivitiesService {

    private final IStudentGetByEmailService studentGetByEmailService;
    
    private final Map<String, IRankingByActivitiesStrategyService> strategies;

    @Override
    public LeaderboardResponseDto cu121rankingByActivities(LeaderboardRequestDto leaderboardRequestDto, User user) {
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        IRankingByActivitiesStrategyService strategy = strategies.get("ACTIVITIES_" + leaderboardRequestDto.getType().name());

        if (strategy == null) {
            throw new ConflictException("Tipo de ranking de actividades no soportado: " + leaderboardRequestDto.getType().name());
        }

        return strategy.execute(leaderboardRequestDto, student);
        
    }
}
