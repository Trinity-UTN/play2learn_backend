package trinity.play2learn.backend.activity.activity.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
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


    @Override
    @Transactional
    public ActivityCompletedResponseDto cu61ActivityCompleted(ActivityCompletedRequestDto activityCompletedRequestDto, User user) {

        Activity activity = activityFindByIdService.findActivityById(activityCompletedRequestDto.getActivityId());
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        //Valida que la actividad este publicada(fecha actual dentro de la fecha de inicio y fin de la actividad)
        activityValidatePublishedStatusService.validatePublishedStatus(activity);

        //Valida que la actividad no haya sido aprobada
        ActivityCompletedState activityCompletedState = activityGetCompletedStateService.getActivityCompletedState(activity, student);
        if (activityCompletedState == ActivityCompletedState.APPROVED) {
            throw new ConflictException("La actividad ya ha sido aprobada.");
        }

        Optional<ActivityCompleted> lastStarted = activityCompletedGetLastStartedService.getLastStartedInProgress(activity, student);
        
        if (lastStarted.isEmpty()){
            throw new ConflictException("No se puede actualizar la actividad ya que no se encuentra en curso.");
        }

        // Si el tiempo de intento es mayor al tiempo maximo de la actividad, se desaprueba automaticamente
        if (this.calculateTimeAttemp(lastStarted.get().getStartedAt()) > activity.getMaxTime()){
            activityCompletedRequestDto.setState(ActivityCompletedState.DISAPPROVED);
        }

        IActivityCompletedStrategyService strategyService = activityCompletedStrategyServiceMap.get(activityCompletedRequestDto.getState().name());

        return strategyService.execute(lastStarted.get());
    }


    private int calculateTimeAttemp (LocalDateTime startedAt){
        return (int) (Duration.between(startedAt, LocalDateTime.now()).getSeconds())/60;
    }
    
    
}
