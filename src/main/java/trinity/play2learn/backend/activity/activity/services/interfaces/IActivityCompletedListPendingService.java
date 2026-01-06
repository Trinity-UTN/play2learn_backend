package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedPendingDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityCompletedListPendingService {
    
    List<ActivityCompletedPendingDto> cu128ListPendingActivities(User user);
}
