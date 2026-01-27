package trinity.play2learn.backend.activity.activity.services.student;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNoLudicaStartService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileUpdateLevelService;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.configs.levels.ValueXp;

@Service
@RequiredArgsConstructor
public class ActivityCompletedService implements IActivityCompletedService {

    private final IActivityGetByIdService activityFindByIdService;

    private final Map<String, IActivityCompletedStrategyService> activityCompletedStrategyServiceMap;

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IActivityValidatePublishedStatusService activityValidatePublishedStatusService;

    private final IActivityGetCompletedStateService activityGetCompletedStateService;

    private final IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;

    private final IProfileUpdateLevelService profileUpdateLevelService;

    @Autowired
    @Lazy
    private IActivityNoLudicaStartService activityNoLudicaStartService;

    // Map que sirve para mapear una Dificultad a un valor de XP
    private final Map<Difficulty, ValueXp> difficultyXpMap = Map.of(
        Difficulty.FACIL, ValueXp.ACTIVITY_FACIL,
        Difficulty.MEDIO, ValueXp.ACTIVITY_MEDIO,
        Difficulty.DIFICIL, ValueXp.ACTIVITY_DIFICIL
    );

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

        /*
        En caso de que la actividad sea no ludica, se inicia en el momento
        en que se completa, esto para saltar la verificacion de los otros tipos de actividades
        los cuales deben estar en curso para poder completarse
        */
        activityNoLudicaStartService.startNoLudicaActivity(user, activity);

        Optional<ActivityCompleted> lastStartedOp = activityCompletedGetLastStartedService
                .getLastStartedInProgress(activity, student);

        if (lastStartedOp.isEmpty()) {
            throw new ConflictException("No se puede actualizar la actividad ya que no se encuentra en curso.");
        }

        // Si la actividad tiene un tiempo maximo, se valida que el tiempo de intento
        // sea menor al tiempo maximo
        if (activity.getMaxTime() > 0) {

            // Si el tiempo de intento es mayor al tiempo maximo de la actividad, se
            // desaprueba automaticamente
            if (this.calculateTimeAttemp(lastStartedOp.get().getStartedAt()) > activity.getMaxTime()) {
                activityCompletedRequestDto.setState(ActivityCompletedState.DISAPPROVED);
            }
        }

        ActivityCompleted lastStarted = lastStartedOp.get();

        lastStarted.setScore(activityCompletedRequestDto.getScore());
        lastStarted.setCorrectAnswers(activityCompletedRequestDto.getCorrectAnswers());
        lastStarted.setIncorrectAnswers(activityCompletedRequestDto.getIncorrectAnswers());
        lastStarted.setUnanswered(activityCompletedRequestDto.getUnanswered());

        IActivityCompletedStrategyService strategyService = activityCompletedStrategyServiceMap
                .get(activityCompletedRequestDto.getState().name());

        // Experiencia obtenida por la actividad, depende de la dificultad
        if (activity.getDifficulty() != null) {
            ValueXp valueXp = difficultyXpMap.get(activity.getDifficulty());
            profileUpdateLevelService.execute(student.getProfile(), valueXp.getValue());
        } else {
            profileUpdateLevelService.execute(student.getProfile(), ValueXp.ACTIVITY_FACIL.getValue());
        }

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
