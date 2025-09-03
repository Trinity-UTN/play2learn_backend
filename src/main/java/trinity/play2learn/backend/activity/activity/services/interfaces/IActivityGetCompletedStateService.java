package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityGetCompletedStateService {
    
    ActivityCompletedState getActivityCompletedState(Activity activity, Student student);
}
