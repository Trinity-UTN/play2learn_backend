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
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetSubjectTotalRewardService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.admin.subject.models.Subject;
import java.util.ArrayList;
import java.util.List;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByCoinsMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;

@Service ("MATERIA")    
@AllArgsConstructor
public class RankingByCoinsSubjectService implements IRankingByCoinsStrategyService {
    
    private final ISubjectGetByIdService subjectGetByIdService;
    private final IActivityCompletedGetSubjectTotalRewardService activityCompletedGetSubjectTotalRewardService;
    private final IRankingGetTop10StudentsService rankingGetTop10StudentsService;
    private final IRankingGetStudentPositionService rankingGetStudentPositionService;
    private final IRankingFindCoinsDtoService rankingFindCoinsDtoService;
    
    @Override
    public LeaderboardResponseDto execute(LeaderboardRequestDto leaderboardRequestDto, Student student) {

        if (leaderboardRequestDto.getId() == null) {
            throw new ConflictException("El ID de la materia es requerido para el ranking de materia");
        }

        Subject subject = subjectGetByIdService.findById(leaderboardRequestDto.getId());

        if (!subject.getStudents().contains(student)) {
            throw new ConflictException("El estudiante no puede ver el ranking de esta materia");
        }

        List<Student> students = subject.getStudents();

        //Crea un listado de dtos con el estudiante y el total de recompensas acumuladas únicamente en esta materia.
        List<StudentWithRewardTotalDto> studentsWithRewardTotal = new ArrayList<>();
        for (Student s : students) {

            //Calcula el total de recompensas acumuladas únicamente en esta materia por el estudiante.
            Double subjectRankingBalance = activityCompletedGetSubjectTotalRewardService.getSubjectTotalRewardByStudent(s, subject);

            studentsWithRewardTotal.add(LeaderboardByCoinsMapper.toStudentWithRewardTotalDto(s, subjectRankingBalance));
        }
        
        //Obtiene el top 10 de estudiantes
        List<StudentWithRewardTotalDto> top10Students = rankingGetTop10StudentsService.getTop10StudentsByCoins(studentsWithRewardTotal);

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByCoinsMapper.toDtoListByStudentWithRewardTotalDto(top10Students);

        StudentWithRewardTotalDto studentDto = rankingFindCoinsDtoService.findDtoByStudent(studentsWithRewardTotal, student);

        //Obtiene la posicion del estudiante en el ranking
        Long studentPosition = rankingGetStudentPositionService.getStudentRankingPositionByCoins(studentsWithRewardTotal, studentDto);

        LeaderboardParticipantResponseDto currentUserPosition = LeaderboardByCoinsMapper.toParticipantDto(
            studentDto,
            studentPosition
        );

        return LeaderboardByCoinsMapper.toDto(participants, currentUserPosition, students.size());
    }

    
}
