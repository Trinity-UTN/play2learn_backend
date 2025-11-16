package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentGetResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateStudentGetDtosService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCreateStudentGetDtosService implements IActivityCreateStudentGetDtosService {

    private final IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;

    private final IActivityCountCompletedService activityCountCompletedService;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityStudentGetResponseDto> createStudentGetDtos(Activity activity) {

        List<ActivityStudentGetResponseDto> activityStudentGetDtos = new ArrayList<>();

        for (Student student : activity.getSubject().getStudents()) {

            Optional<ActivityCompleted> optionalActivityCompleted = activityCompletedGetLastStartedService.getLastStarted(activity, student); 
            //Obtiene el ultimo intento iniciado

            String studentName = student.getCompleteName();

            //Si no hay intentos, la actividad esta en estado no completada
            if (optionalActivityCompleted.isEmpty()) {
                activityStudentGetDtos.add(ActivityCompletedMapper.toStudentGetDto(studentName,
                        ActivityCompletedState.NOT_COMPLETED, 0, 0.0));
                continue;
            }

            ActivityCompleted activityCompleted = optionalActivityCompleted.get();

            ActivityCompletedState state = activityCompleted.getState();
            Double reward = activityCompleted.getReward();

            int attempts = activityCountCompletedService.countCompletedByActivityAndStudent(activity, student); 
            //Cuenta cuantos intentos realizo un estudiante de una actividad

            // Si desaprobo el ultimo intento pero le quedan intentos restantes, la
            // actividad esta en progreso
            if (activityCompleted.getState() == ActivityCompletedState.DISAPPROVED
                    && activityCompleted.getRemainingAttempts() > 0) {
                state = ActivityCompletedState.IN_PROGRESS;
            }

            activityStudentGetDtos.add(ActivityCompletedMapper.toStudentGetDto(studentName, state, attempts, reward));
        }

        // Ordeno segun el estado (Aprobadas, en progreso, desaprobadas, no completadas)
        activityStudentGetDtos.sort(Comparator.comparingInt(dto -> dto.getState().ordinal())); //Ordena segun el orden del enum

        return activityStudentGetDtos;
    }

}
