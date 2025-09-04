package trinity.play2learn.backend.activity.activity.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
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
    private final IActivityGetRemainingAttemptsService activityGetRemainingAttemptsService;

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
        
        //Valida que el estudiante tenga intentos restantes
        Integer remainingAttempts = activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student);
        if (remainingAttempts == 0) {
            throw new ConflictException("No quedan intentos para realizar la actividad.");
            
        }

        IActivityCompletedStrategyService strategyService = activityCompletedStrategyServiceMap.get(activityCompletedRequestDto.getState().name());

        return strategyService.execute(activity, student, remainingAttempts);
    }
    
    
}
