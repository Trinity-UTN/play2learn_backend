package trinity.play2learn.backend.profile.ranking.services.RankingByCoinsStrategies;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsStrategyService;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetRankingByStudentsService;
import java.util.List;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByCoinsMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;

import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetPositionRankingByStudentsService;

@Service ("MATERIA")    
@AllArgsConstructor
public class RankingByCoinsSubjectService implements IRankingByCoinsStrategyService {
    

    private final ISubjectGetByIdService subjectGetByIdService;

    private final IWalletGetRankingByStudentsService walletGetRankingByStudentsService;

    private final IWalletGetPositionRankingByStudentsService walletGetPositionRankingByStudentsService;


    @Override
    public LeaderboardResponseDto execute(LeaderboardRequestDto leaderboardRequestDto, Student student) {

        if (leaderboardRequestDto.getId() == null) {
            throw new ConflictException("El ID de la materia es requerido para el ranking de materia");
        }

        Subject subject = subjectGetByIdService.findById(leaderboardRequestDto.getId());

        if (!subject.getStudents().contains(student)) {
            throw new ConflictException("El estudiante no puede ver el ranking de esta materia");
        }

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByCoinsMapper.toDtoList(
            walletGetRankingByStudentsService.execute(subject.getStudents())
        );

        LeaderboardParticipantResponseDto currentUserPosition = LeaderboardByCoinsMapper.toParticipantDto(
            student.getWallet(),
            walletGetPositionRankingByStudentsService.execute(student, subject.getStudents())
        );

        return LeaderboardByCoinsMapper.toDto(participants, currentUserPosition);
    }
}
