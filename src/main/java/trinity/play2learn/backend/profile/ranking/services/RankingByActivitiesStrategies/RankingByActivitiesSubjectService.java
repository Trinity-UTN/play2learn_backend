package trinity.play2learn.backend.profile.ranking.services.RankingByActivitiesStrategies;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByActivitiesStrategyService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingFindDtoService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetStudentPositionService;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetTop10StudentsService;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;
import trinity.play2learn.backend.profile.ranking.dtos.request.LeaderboardRequestDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardByCoinsMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import java.util.ArrayList;
import java.util.List;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetSubjectTotalService;

@Service("ACTIVITIES_MATERIA")
@AllArgsConstructor
public class RankingByActivitiesSubjectService implements IRankingByActivitiesStrategyService {

    private final ISubjectGetByIdService subjectGetByIdService;
    private final IActivityCompletedGetSubjectTotalService activityCompletedGetSubjectTotalService;
    private final IRankingGetTop10StudentsService rankingGetTop10StudentsService;
    private final IRankingFindDtoService rankingFindCoinsDtoService;
    private final IRankingGetStudentPositionService rankingGetStudentPositionService;

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

        // Crea un listado de dtos con el estudiante y el total de recompensas
        // acumuladas únicamente en esta materia.
        List<StudentWithTotalDto> studentsWithTotal = new ArrayList<>();
        for (Student s : students) {

            // Cuenta las actividades de la materia aprobadas por el estudiante
            Double subjectRankingBalance = activityCompletedGetSubjectTotalService
                    .getSubjectTotalApprovedByStudent(s, subject);

            studentsWithTotal.add(LeaderboardByCoinsMapper.toStudentWithTotalDto(s, subjectRankingBalance));
        }

        // Obtiene el top 10 de estudiantes
        List<StudentWithTotalDto> top10Students = rankingGetTop10StudentsService
                .getTop10StudentsByCoins(studentsWithTotal);

        List<LeaderboardParticipantResponseDto> participants = LeaderboardByCoinsMapper
                .toDtoListByStudentWithTotalDto(top10Students);

        StudentWithTotalDto studentDto = rankingFindCoinsDtoService.findDtoByStudent(studentsWithTotal,
                student);

        // Obtiene la posicion del estudiante en el ranking
        Long studentPosition = rankingGetStudentPositionService
                .getStudentRankingPositionByCoins(studentsWithTotal, studentDto);

        LeaderboardParticipantResponseDto currentUserPosition = LeaderboardByCoinsMapper.toParticipantDto(
                studentDto,
                studentPosition);

        return LeaderboardByCoinsMapper.toDto(participants, currentUserPosition, students.size());
    }
}
