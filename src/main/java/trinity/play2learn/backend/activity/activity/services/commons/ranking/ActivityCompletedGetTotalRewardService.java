package trinity.play2learn.backend.activity.activity.services.commons.ranking;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetTotalRewardService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;

@Service
@AllArgsConstructor
public class ActivityCompletedGetTotalRewardService implements IActivityCompletedGetTotalRewardService {

    private final IActivityCompletedRepository activityCompletedRepository;

    // Trae la suma de las monedas historicas obtenidas por un estudiante (Para el
    // ranking de monedas por institucion y curso)
    @Override
    public Double getTotalRewardByStudent(Student student) {
        return activityCompletedRepository.sumRewardByStateAndStudent(ActivityCompletedState.APPROVED, student);
    }

    @Override
    public List<StudentWithTotalDto> getTotalRewardByStudents(List<Student> students) {

        // Realiza una consulta optimizada a la base de datos para obtener el total de
        // recompensas obtenidas por cada estudiante en una sola consulta.
        Map<Student, Double> rewardByStudent = activityCompletedRepository
                .sumRewardByStateGroupedByStudent(ActivityCompletedState.APPROVED)
                .stream()
                .collect(Collectors.toMap(
                        StudentWithTotalDto::getStudent,
                        StudentWithTotalDto::getTotal));

        // Agrega los que no tienen ninguna actividad con 0.0
        return students.stream()
                .map(s -> new StudentWithTotalDto(s, rewardByStudent.getOrDefault(s, 0.0)))
                .collect(Collectors.toList());
    }
}
