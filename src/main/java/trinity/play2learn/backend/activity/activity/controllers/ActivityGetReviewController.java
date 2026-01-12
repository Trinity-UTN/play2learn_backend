package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityReviewResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetReviewService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/activity/student/review")
public class ActivityGetReviewController {

    private final IActivityGetReviewService activityGetReviewService;

    @GetMapping("/{activityId}")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<ActivityReviewResponseDto>> getReview(
        @PathVariable Long activityId, 
        @SessionUser User user
    ) {
        return ResponseFactory.ok(
            activityGetReviewService.cu130GetReview(
                user, 
                activityId
            ), 
            SuccessfulMessages.okSuccessfully()
        );
    }
}
