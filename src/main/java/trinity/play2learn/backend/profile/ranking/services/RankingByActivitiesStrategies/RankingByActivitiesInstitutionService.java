package trinity.play2learn.backend.profile.ranking.services.RankingByActivitiesStrategies;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetTotalRankingService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetPositionRankingService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByActivitiesStrategyService;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentFindAllService;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByActivitiesMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import java.util.List;

@Service ("ACTIVITIES_INSTITUCION")
@AllArgsConstructor
public class RankingByActivitiesInstitutionService implements IRankingByActivitiesStrategyService {

    private final IActivityGetTotalRankingService activityGetTotalRankingService;

    private final IActivityGetPositionRankingService activityGetPositionRankingService;
    private final IStudentFindAllService studentFindAllService;

    @Override
    public LeaderboardResponseDto execute (LeaderboardRequestDto leaderboardRequestDto, Student student) {
        
        List<Student> students = studentFindAllService.findAll();
        Integer totalParticipants = students.size();

        List<Object[]> results = activityGetTotalRankingService.execute();

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByActivitiesMapper.toParticipantDtoList(results);

        Object[] positionResult = activityGetPositionRankingService.execute(student);
        
        LeaderboardParticipantResponseDto currentUserPosition = null;
        if (positionResult != null && positionResult.length >= 2) {
            Object countObj = positionResult[1];
            Double count = countObj instanceof Long ? ((Long) countObj).doubleValue() : (Double) countObj;
            currentUserPosition = LeaderboardByActivitiesMapper.toParticipantDto(student, (Long) positionResult[0], count);
        }

        return LeaderboardByActivitiesMapper.toDto(participants, currentUserPosition, totalParticipants);
    }
}
