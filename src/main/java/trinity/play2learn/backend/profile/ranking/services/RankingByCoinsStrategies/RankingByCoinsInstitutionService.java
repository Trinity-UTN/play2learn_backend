package trinity.play2learn.backend.profile.ranking.services.RankingByCoinsStrategies;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsStrategyService;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetPositionRankingService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetTotalRakingService;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByCoinsMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;

@Service ("INSTITUCION")
@AllArgsConstructor
public class RankingByCoinsInstitutionService implements IRankingByCoinsStrategyService {

    private final IWalletGetTotalRakingService walletGetTotalRakingService;

    private final IWalletGetPositionRankingService walletGetPositionRankingService;


    @Override
    public LeaderboardResponseDto execute(LeaderboardRequestDto leaderboardRequestDto, Student student) {

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByCoinsMapper.toDtoList(
            walletGetTotalRakingService.execute()
        );

        LeaderboardParticipantResponseDto currentUserPosition = LeaderboardByCoinsMapper.toParticipantDto(
            student.getWallet(),
            walletGetPositionRankingService.execute(student)
        );

        return LeaderboardByCoinsMapper.toDto(participants, currentUserPosition);
    }
    
}
