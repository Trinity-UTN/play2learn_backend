package trinity.play2learn.backend.profile.ranking.services.RankingByCoinsStrategies;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsStrategyService;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByCourseService;
import java.util.List;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByCoinsMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetRankingByStudentsService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetPositionRankingByStudentsService;

@Service ("CURSO")
@AllArgsConstructor
public class RankingByCoinsCourseService implements IRankingByCoinsStrategyService {

    private final IStudentGetByCourseService studentGetByCourseService;

    private final IWalletGetRankingByStudentsService walletGetRankingByStudentsService;

    private final IWalletGetPositionRankingByStudentsService walletGetPositionRankingByStudentsService;
    
    @Override
    public LeaderboardResponseDto execute(LeaderboardRequestDto leaderboardRequestDto, Student student) {

        List<Student> students = studentGetByCourseService.getStudentsByCourseId(student.getCourse().getId());

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByCoinsMapper.toDtoList(
            walletGetRankingByStudentsService.execute(students)
        );

        LeaderboardParticipantResponseDto currentUserPosition = LeaderboardByCoinsMapper.toParticipantDto(
            student.getWallet(),
            walletGetPositionRankingByStudentsService.execute(student, students)
        );

        return LeaderboardByCoinsMapper.toDto(participants, currentUserPosition);
    }
}