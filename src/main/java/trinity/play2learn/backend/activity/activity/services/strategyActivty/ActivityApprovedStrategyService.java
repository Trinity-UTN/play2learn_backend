package trinity.play2learn.backend.activity.activity.services.strategyActivty;

import org.springframework.stereotype.Service;

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

@Service("APPROVED")
@AllArgsConstructor
public class ActivityApprovedStrategyService implements IActivityCompletedStrategyService {
    
    private final IActivityCompletedRepository activityCompletedRepository;
    private final IActivityGetRemainingAttemptsService getRemainingAttemptsService;

    @Override
    public ActivityCompletedResponseDto execute(Activity activity, Student student) {

        Integer remainingAttempts = getRemainingAttemptsService.getStudentRemainingAttempts(activity, student);

        //Aca se llamaria al servicio de calcular la recompensa cuando este implementado segun como entregue recompensa la actividad (Strategy)
        Double reward = 0.0;
        
        ActivityCompleted activityCompleted = ActivityCompletedMapper.toModel(activity, student, reward, remainingAttempts, ActivityCompletedState.APPROVED);

        return ActivityCompletedMapper.toDto(activityCompletedRepository.save(activityCompleted));
        
    }
    
    
}
