package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;

public interface IActivityGetCompletedByIdService {
    
    ActivityCompleted findActivityCompletedById(Long activityCompletedId);
}
