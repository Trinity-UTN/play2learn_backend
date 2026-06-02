package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityReviewNoLudicaRequestDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityTeacherReviewNoLudicaService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("activity/teacher")
public class ActivityReviewNoLudicaController {
    
    private final IActivityTeacherReviewNoLudicaService activityTeacherReviewNoLudicaService;
    
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    @PostMapping("/review-no-ludica")
    public ResponseEntity<BaseResponse<ActivityCompletedResponseDto>> reviewNoLudicaAttempt(
        @SessionUser User user, 
        @Valid @RequestBody ActivityReviewNoLudicaRequestDto activityReviewNoLudicaDto) {
        
        return ResponseFactory.created(
            activityTeacherReviewNoLudicaService.cu129TeacherReviewNoLudica(user, activityReviewNoLudicaDto), 
            SuccessfulMessages.updatedSuccessfully("Actividad completada"));
    }
    
}
