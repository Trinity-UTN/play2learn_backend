package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityCompletedStrategyService {
    
    ActivityCompletedResponseDto execute(Activity activity, Student student);
}
