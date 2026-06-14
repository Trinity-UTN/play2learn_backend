package trinity.play2learn.backend.profile.ranking.services.RankingByActivitiesStrategies;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByActivitiesStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRankingByStudentsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetPositionRankingByStudentsService;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByActivitiesMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import java.util.List;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByCourseService;

@Service ("ACTIVITIES_CURSO")
@AllArgsConstructor
public class RankingByActivitiesCourseService implements IRankingByActivitiesStrategyService {

    private final IActivityGetRankingByStudentsService activityGetRankingByStudentsService;

    private final IActivityGetPositionRankingByStudentsService activityGetPositionRankingByStudentsService;

    private final IStudentGetByCourseService studentGetByCourseService;
    
    @Override
    public LeaderboardResponseDto execute (LeaderboardRequestDto leaderboardRequestDto, Student student) {
       
        List<Student> students = studentGetByCourseService.getStudentsByCourseId(student.getCourse().getId());
        
        List<Object[]> results = activityGetRankingByStudentsService.execute(students);

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByActivitiesMapper.toParticipantDtoList(results);

        Object[] positionResult = activityGetPositionRankingByStudentsService.execute(student, students);
        
        LeaderboardParticipantResponseDto currentUserPosition = null;
        if (positionResult != null && positionResult.length >= 2) {
            Object countObj = positionResult[1];
            Double count = countObj instanceof Long ? ((Long) countObj).doubleValue() : (Double) countObj;
            currentUserPosition = LeaderboardByActivitiesMapper.toParticipantDto(student, (Long) positionResult[0], count);
        }

        return LeaderboardByActivitiesMapper.toDto(participants, currentUserPosition, students.size());
    }
}
