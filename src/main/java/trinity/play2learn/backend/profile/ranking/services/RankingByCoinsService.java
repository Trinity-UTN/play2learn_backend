package trinity.play2learn.backend.profile.ranking.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsStrategyService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class RankingByCoinsService implements IRankingByCoinsService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final Map<String, IRankingByCoinsStrategyService> strategies;

    @Override
    public LeaderboardResponseDto cu120rankingByCoins(LeaderboardRequestDto leaderboardRequestDto, User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        IRankingByCoinsStrategyService strategy = strategies.get(leaderboardRequestDto.getType().name());

        if (strategy == null) {
            throw new ConflictException("Tipo de ranking no soportado: " + leaderboardRequestDto.getType().name());
        }

        return strategy.execute(leaderboardRequestDto, student);
    }
}
