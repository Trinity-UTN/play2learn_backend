package trinity.play2learn.backend.profile.ranking.services.RankingByCoinsStrategies;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsStrategyService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingFindDtoService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetStudentPositionService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetTop10StudentsService;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetTotalRewardService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentFindAllService;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByCoinsMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;

@Service("INSTITUCION")
@AllArgsConstructor
public class RankingByCoinsInstitutionService implements IRankingByCoinsStrategyService {

        private final IActivityCompletedGetTotalRewardService getTotalRewardService;
        private final IRankingGetTop10StudentsService rankingGetTop10StudentsService;
        private final IRankingGetStudentPositionService rankingGetStudentPositionService;
        private final IRankingFindDtoService rankingFindCoinsDtoService;
        private final IStudentFindAllService studentFindAllService;

        @Override
        public LeaderboardResponseDto execute(LeaderboardRequestDto leaderboardRequestDto, Student student) {

                // Trae todos los estudiantes activos. Si agregamos la clase Institucion
                // habria que cambiar este metodo
                List<Student> students = studentFindAllService.findAll();

                // La BD trae todos los estudiantes con sus recompensas acumuladas en una sola
                // consulta (optimizacion)
                List<StudentWithTotalDto> studentsWithRewardTotal = getTotalRewardService
                                .getTotalRewardByStudents(students);

                // Obtiene el top 10 de estudiantes
                List<StudentWithTotalDto> top10Students = rankingGetTop10StudentsService
                                .getTop10StudentsByCoins(studentsWithRewardTotal);

                List<LeaderboardParticipantResponseDto> participants = LeaderboardByCoinsMapper
                                .toDtoListByStudentWithTotalDto(top10Students);

                StudentWithTotalDto studentDto = rankingFindCoinsDtoService.findDtoByStudent(studentsWithRewardTotal,
                                student);

                // Obtiene la posicion del estudiante en el ranking
                Long studentPosition = rankingGetStudentPositionService
                                .getStudentRankingPositionByCoins(studentsWithRewardTotal, studentDto);

                LeaderboardParticipantResponseDto currentUserPosition = LeaderboardByCoinsMapper.toParticipantDto(
                                studentDto,
                                studentPosition);

                return LeaderboardByCoinsMapper.toDto(participants, currentUserPosition, students.size());
        }
}
