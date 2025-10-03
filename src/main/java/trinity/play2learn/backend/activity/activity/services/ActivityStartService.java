package trinity.play2learn.backend.activity.activity.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedRequestMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStartService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class ActivityStartService implements IActivityStartService{

    private final IStudentGetByEmailService studentGetByEmailService;   
    
    private final IActivityGetByIdService activityFindByIdService;

    private final IActivityValidatePublishedStatusService activityValidatePublishedStatusService;

    private final IActivityGetCompletedStateService activityGetCompletedStateService;

    private final IActivityGetRemainingAttemptsService activityGetRemainingAttemptsService;

    private final IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;

    private final IActivityCompletedRepository activityCompletedRepository;

    private final IActivityCompletedService activityCompletedService;

    @Override
    @Transactional
    public ActivityCompletedResponseDto execute(User user, Long activityId) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Activity activity = activityFindByIdService.findActivityById(activityId);

        activityValidatePublishedStatusService.validatePublishedStatus(activity);

        ActivityCompletedState activityCompletedState = activityGetCompletedStateService.getActivityCompletedState(activity, student);
        
        if (activityCompletedState == ActivityCompletedState.APPROVED) {
            throw new ConflictException("La actividad ya ha sido aprobada.");
        }

        Integer remainingAttempts = activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student);

        if (remainingAttempts == 0) {
            throw new ConflictException("No quedan intentos para realizar la actividad.");
        }

        // Valido si no hay alguna realizacion en curso
        // En caso de que exista, la finalizo como desaprobada\
        // Luego valido, si luego de desaprobar la misma, quedan intentos
        // En caso de que no queden intentos, no dejo iniciar una nueva
        Optional<ActivityCompleted> lastStartedOpt = activityCompletedGetLastStartedService.get(activity, student);
        if (lastStartedOpt.isPresent()) {
            ActivityCompletedResponseDto attemptDisapproved = activityCompletedService.cu61ActivityCompleted(
                ActivityCompletedRequestMapper.toDto(
                    activity.getId(),
                    ActivityCompletedState.DISAPPROVED
                ),
                user
            );
            remainingAttempts = attemptDisapproved.getRemainingAttempts();
            if (attemptDisapproved.getRemainingAttempts() == 0) {
                throw new ConflictException("No quedan intentos para realizar la actividad.");
            }
        }


        return ActivityCompletedMapper.toDto(
            activityCompletedRepository.save(
                ActivityCompletedMapper.toModel(
                    activity, 
                    student, 
                    null, 
                    remainingAttempts, 
                    ActivityCompletedState.IN_PROGRESS
                )
            )
        );
    }
}
