package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;

public interface IActivityGetStatusService {
    
    ActivityStatus getStatus(Activity activity);
}
