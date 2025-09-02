package trinity.play2learn.backend.activity.activity.services.strategyActivty;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service("DISAPPROVED")
@AllArgsConstructor
public class ActivityDisapprovedStrategyService implements IActivityCompletedStrategyService{
    
    private final IActivityCompletedRepository activityCompletedRepository;
    private final IActivityGetRemainingAttemptsService getRemainingAttemptsService;

    @Override
    @Transactional
    public ActivityCompletedResponseDto execute(Activity activity, Student student) {
        
        //Obtiene los intentos restantes del estudiante en la actividad y le resta 1, ya que la desaprobo
        Integer remainingAttempts = getRemainingAttemptsService.getStudentRemainingAttempts(activity, student) - 1;
        
        ActivityCompleted activityCompleted = ActivityCompletedMapper.toModel(activity, student, null, remainingAttempts, ActivityCompletedState.DISAPPROVED);
        
        return ActivityCompletedMapper.toDto(activityCompletedRepository.save(activityCompleted));
    }

    
}
