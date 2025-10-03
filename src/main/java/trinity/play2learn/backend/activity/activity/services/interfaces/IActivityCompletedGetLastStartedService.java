package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.Optional;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityCompletedGetLastStartedService {

    public Optional<ActivityCompleted> get (Activity activity, Student student);
    
}
