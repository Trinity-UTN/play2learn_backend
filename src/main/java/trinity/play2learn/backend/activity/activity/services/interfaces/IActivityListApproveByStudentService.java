package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentStateResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityListApproveByStudentService {

    List<ActivityStudentStateResponseDto> cu63ListApprovedActivitiesByStudent(User user);
}
