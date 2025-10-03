package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStartService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/activity/start")
public class ActivityStartedController {

    private final IActivityStartService activityStartService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<ActivityCompletedResponseDto>> activityStart(
        @Valid 
        @RequestParam Long activityId, 
        @SessionUser User user
    ){ 
        return ResponseFactory.created(
            activityStartService.execute(
                user, 
                activityId
            ),
            SuccessfulMessages.createdSuccessfully("Actividad iniciada")
        );
    }
    
}
