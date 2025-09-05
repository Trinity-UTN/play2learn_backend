package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@AllArgsConstructor
@RequestMapping("/activity")
public class ActivityGetController {
    
    private final IActivityGetService activityGetService;

    @GetMapping("/{id}")
    @SessionRequired(roles = Role.ROLE_STUDENT)
    public ResponseEntity<BaseResponse<ActivityResponseDto>> getActivity(@PathVariable Long id) {

        return ResponseFactory.ok(activityGetService.cu64GetActivity(id), SuccessfulMessages.okSuccessfully());
    }
    
}
