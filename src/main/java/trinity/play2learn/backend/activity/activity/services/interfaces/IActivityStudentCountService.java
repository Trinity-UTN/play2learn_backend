package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentCountResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityStudentCountService {
    
    ActivityStudentCountResponseDto cu88CountActivitiesPerState(User user);
}
