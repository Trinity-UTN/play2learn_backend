package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityGetNotApprovedByStudentService {
    
    List<ActivityStudentNotApprovedResponseDto> cu62ListNotApprovedActivitiesByStudent(User user);
}
