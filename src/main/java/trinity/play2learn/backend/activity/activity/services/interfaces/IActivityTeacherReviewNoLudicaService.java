package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityReviewNoLudicaRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityTeacherReviewNoLudicaService {

    ActivityCompletedResponseDto cu129TeacherReviewNoLudica(User user,
            ActivityReviewNoLudicaRequestDto activityCompleteNoLudicaDto);
}
