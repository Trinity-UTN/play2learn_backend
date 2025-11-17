package trinity.play2learn.backend.activity.activity.services.strategyActivty;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;

@Service("PENDING")
@AllArgsConstructor
public class ActivityPendingStrategyService implements IActivityCompletedStrategyService{
    
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    @Transactional
    public ActivityCompletedResponseDto execute(ActivityCompleted activityCompleted) {
        
        activityCompleted.setRemainingAttempts(activityCompleted.getRemainingAttempts()-1);
        
        activityCompleted.setState(ActivityCompletedState.PENDING);

        activityCompleted.setCompletedAt(LocalDateTime.now());
        
        return ActivityCompletedMapper.toDto(activityCompletedRepository.save(activityCompleted));
    }
    
}
