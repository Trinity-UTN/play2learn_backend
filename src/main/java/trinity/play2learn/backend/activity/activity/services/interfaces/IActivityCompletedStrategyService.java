package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;

public interface IActivityCompletedStrategyService {
    
    ActivityCompletedResponseDto execute(ActivityCompleted activityCompleted);
}
