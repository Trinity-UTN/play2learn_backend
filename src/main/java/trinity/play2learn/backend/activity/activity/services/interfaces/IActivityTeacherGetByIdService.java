package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherGetResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityTeacherGetByIdService {
    
    ActivityTeacherGetResponseDto cu112TeacherGetActivityById(User user, Long id);
}
