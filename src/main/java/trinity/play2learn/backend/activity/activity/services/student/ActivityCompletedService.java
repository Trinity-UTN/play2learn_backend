package trinity.play2learn.backend.activity.activity.services.student;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityCompletedService implements IActivityCompletedService {

    private final IActivityGetByIdService activityFindByIdService;

    private final Map<String, IActivityCompletedStrategyService> activityCompletedStrategyServiceMap;

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IActivityValidatePublishedStatusService activityValidatePublishedStatusService;

    private final IActivityGetCompletedStateService activityGetCompletedStateService;

    private final IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;

    // Aisla la transaccion para que no se pierda la transaccion de desaprobar el
    // ultimo intento
    // Si el el metodo para iniciar una actividad lanza una excepcion, no se pierde
    // esta transaccion
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public ActivityCompletedResponseDto cu61ActivityCompleted(ActivityCompletedRequestDto activityCompletedRequestDto,
            User user) {

        Activity activity = activityFindByIdService.findActivityById(activityCompletedRequestDto.getActivityId());

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        // Valida que la actividad este publicada(fecha actual dentro de la fecha de
        // inicio y fin de la actividad)
        activityValidatePublishedStatusService.validatePublishedStatus(activity);

        // Valida que la actividad no haya sido aprobada
        ActivityCompletedState activityCompletedState = activityGetCompletedStateService
                .getActivityCompletedState(activity, student);
        if (activityCompletedState == ActivityCompletedState.APPROVED) {
            throw new ConflictException("La actividad ya ha sido aprobada.");
        }

        Optional<ActivityCompleted> lastStartedOp = activityCompletedGetLastStartedService
                .getLastStartedInProgress(activity, student);

        if (lastStartedOp.isEmpty()) {
            throw new ConflictException("No se puede actualizar la actividad ya que no se encuentra en curso.");
        }

        // Si el tiempo de intento es mayor al tiempo maximo de la actividad, se
        // desaprueba automaticamente
        if (this.calculateTimeAttemp(lastStartedOp.get().getStartedAt()) > activity.getMaxTime()) {
            activityCompletedRequestDto.setState(ActivityCompletedState.DISAPPROVED);
        }

        ActivityCompleted lastStarted = lastStartedOp.get();

        lastStarted.setScore(activityCompletedRequestDto.getScore());
        lastStarted.setCorrectAnswers(activityCompletedRequestDto.getCorrectAnswers());
        lastStarted.setIncorrectAnswers(activityCompletedRequestDto.getIncorrectAnswers());
        lastStarted.setUnanswered(activityCompletedRequestDto.getUnanswered());

        IActivityCompletedStrategyService strategyService = activityCompletedStrategyServiceMap
                .get(activityCompletedRequestDto.getState().name());

        return strategyService.execute(lastStarted);
    }

    private int calculateTimeAttemp(LocalDateTime startedAt) {
        if (startedAt == null) {
            return 0;
        }
        long seconds = Duration.between(startedAt, LocalDateTime.now()).getSeconds();
        return Math.max(0, (int) (seconds / 60)); // Asegurar no negativo
    }
}
