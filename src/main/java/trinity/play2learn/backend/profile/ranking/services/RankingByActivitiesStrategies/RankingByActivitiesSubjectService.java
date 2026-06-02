package trinity.play2learn.backend.profile.ranking.services.RankingByActivitiesStrategies;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByActivitiesStrategyService;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByActivitiesMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import java.util.List;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRankingByStudentsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetPositionRankingByStudentsService;

@Service ("ACTIVITIES_MATERIA")
@AllArgsConstructor
public class RankingByActivitiesSubjectService implements IRankingByActivitiesStrategyService {
    
    private final ISubjectGetByIdService subjectGetByIdService;

    private final IActivityGetRankingByStudentsService activityGetRankingByStudentsService;

    private final IActivityGetPositionRankingByStudentsService activityGetPositionRankingByStudentsService;


    @Override
    public LeaderboardResponseDto execute (LeaderboardRequestDto leaderboardRequestDto, Student student) {

        if (leaderboardRequestDto.getId() == null) {
            throw new ConflictException("El ID de la materia es requerido para el ranking de materia");
        }

        Subject subject = subjectGetByIdService.findById(leaderboardRequestDto.getId());

        if (!subject.getStudents().contains(student)) {
            throw new ConflictException("El estudiante no puede ver el ranking de esta materia");
        }
        

        List<Object[]> results = activityGetRankingByStudentsService.execute(subject.getStudents());

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByActivitiesMapper.toParticipantDtoList(results);

        Object[] positionResult = activityGetPositionRankingByStudentsService.execute(student, subject.getStudents());
        
        LeaderboardParticipantResponseDto currentUserPosition = null;
        if (positionResult != null && positionResult.length >= 2) {
            Object countObj = positionResult[1];
            Double count = countObj instanceof Long ? ((Long) countObj).doubleValue() : (Double) countObj;
            currentUserPosition = LeaderboardByActivitiesMapper.toParticipantDto(student, (Long) positionResult[0], count);
        }

        return LeaderboardByActivitiesMapper.toDto(participants, currentUserPosition);
    }
}
