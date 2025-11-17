package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.Optional;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityCompletedGetLastStartedService {

    Optional<ActivityCompleted> getLastStartedInProgress (Activity activity, Student student);
    
    Optional<ActivityCompleted> getLastStarted(Activity activity, Student student);
}
