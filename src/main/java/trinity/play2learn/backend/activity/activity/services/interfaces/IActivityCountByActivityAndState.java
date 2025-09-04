package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

public interface IActivityCountByActivityAndState {
    
    public int execute (Activity activity, ActivityCompletedState state);
    
}
