package trinity.play2learn.backend.profile.ranking.services.RankingByCoinsStrategies;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsStrategyService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingFindCoinsDtoService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetStudentPositionService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetTop10StudentsService;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithRewardTotalDto;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetTotalRewardService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByCourseService;
import java.util.ArrayList;
import java.util.List;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByCoinsMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;

@Service("CURSO")
@AllArgsConstructor
public class RankingByCoinsCourseService implements IRankingByCoinsStrategyService {

    private final IStudentGetByCourseService studentGetByCourseService;
    private final IActivityCompletedGetTotalRewardService getTotalRewardService;
    private final IRankingGetTop10StudentsService rankingGetTop10StudentsService;
    private final IRankingGetStudentPositionService rankingGetStudentPositionService;
    private final IRankingFindCoinsDtoService rankingFindCoinsDtoService;

    @Override
    public LeaderboardResponseDto execute(LeaderboardRequestDto leaderboardRequestDto, Student student) {

        List<Student> students = studentGetByCourseService.getStudentsByCourseId(student.getCourse().getId());

        //Crea un listado de dtos con el estudiante y el total de recompensas acumuladas
        List<StudentWithRewardTotalDto> studentsWithRewardTotal = new ArrayList<>();
        for (Student s : students) {

                //Calcula el total de recompensas acumuladas por el estudiante en las actividades que aprobo
                Double totalReward = getTotalRewardService.getTotalRewardByStudent(s);

                studentsWithRewardTotal.add(LeaderboardByCoinsMapper.toStudentWithRewardTotalDto(s, totalReward));
        }

        // Obtiene el top 10 de estudiantes
        List<StudentWithRewardTotalDto> top10Students = rankingGetTop10StudentsService.getTop10StudentsByCoins(studentsWithRewardTotal);

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByCoinsMapper.toDtoListByStudentWithRewardTotalDto(top10Students);

        StudentWithRewardTotalDto studentDto = rankingFindCoinsDtoService.findDtoByStudent(studentsWithRewardTotal, student);

        //Obtiene la posicion del estudiante en el ranking
        Long studentPosition = rankingGetStudentPositionService.getStudentRankingPositionByCoins(studentsWithRewardTotal, studentDto);

        LeaderboardParticipantResponseDto currentUserPosition = LeaderboardByCoinsMapper.toParticipantDto(studentDto, studentPosition);

        return LeaderboardByCoinsMapper.toDto(participants, currentUserPosition, students.size());
    }
}