package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("/activity/completed")
public class ActivityCompletedController {
    
    private final IActivityCompletedService activityCompletedService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<ActivityCompletedResponseDto>> activityCompleted(
        @Valid @RequestBody ActivityCompletedRequestDto activityCompletedRequestDto, 
        @SessionUser User user){ 
        
        return ResponseFactory.created(
            activityCompletedService.cu61ActivityCompleted(activityCompletedRequestDto, user), SuccessfulMessages.createdSuccessfully("Actividad completada")
        );
    }
    
}
