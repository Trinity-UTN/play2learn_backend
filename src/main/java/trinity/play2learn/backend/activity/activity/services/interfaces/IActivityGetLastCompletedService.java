package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityGetLastCompletedService {
    
    ActivityCompleted getLastCompleted(Activity activity, Student student);
}
