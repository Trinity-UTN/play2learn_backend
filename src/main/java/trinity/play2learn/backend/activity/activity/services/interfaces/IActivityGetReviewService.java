package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityReviewResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityGetReviewService {

    ActivityReviewResponseDto cu130GetReview(
        User user,
        Long activityId
    );

} 