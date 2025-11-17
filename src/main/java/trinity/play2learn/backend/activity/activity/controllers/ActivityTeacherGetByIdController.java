package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherGetResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityTeacherGetByIdService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/activity/teacher")
public class ActivityTeacherGetByIdController {
    
    private final IActivityTeacherGetByIdService activityTeacherGetByIdService;

    @GetMapping("/{id}")
    @SessionRequired(roles = Role.ROLE_TEACHER)
    public ResponseEntity<BaseResponse<ActivityTeacherGetResponseDto>> getActivityByIdByTeacher(@PathVariable Long id, @SessionUser User user) {
        return ResponseFactory.ok(activityTeacherGetByIdService.cu112TeacherGetActivityById(user, id), SuccessfulMessages.okSuccessfully());
    }
}
