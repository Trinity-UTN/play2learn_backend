package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityCompletedService {
    
    
    ActivityCompletedResponseDto cu61ActivityCompleted(ActivityCompletedRequestDto activityCompletedRequestDto, User user);
}
