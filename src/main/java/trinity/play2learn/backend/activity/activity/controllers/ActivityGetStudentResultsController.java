package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityStudentResultsResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStudentResultsService;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/activity/completed")
public class ActivityGetStudentResultsController {
    
    private final IActivityGetStudentResultsService activityGetStudentResultsService;
    
    @GetMapping("/results/{activityId}")
    @SessionRequired(roles = { Role.ROLE_STUDENT })
    public ResponseEntity<BaseResponse<ActivityStudentResultsResponseDto>> getStudentResults(@PathVariable Long activityId, @SessionUser User user) {
        return ResponseFactory.ok(activityGetStudentResultsService.cu125GetStudentResults(activityId, user), SuccessfulMessages.okSuccessfully());
    }
}
