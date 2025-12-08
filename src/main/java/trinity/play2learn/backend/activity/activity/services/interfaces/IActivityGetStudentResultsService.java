package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityStudentResultsResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityGetStudentResultsService {
    
    ActivityStudentResultsResponseDto cu125GetStudentResults(Long activityId, User user);
}
