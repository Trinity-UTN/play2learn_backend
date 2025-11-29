package trinity.play2learn.backend.notification.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.notification.services.interfaces.INotificationGetByUserService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class GetUserNotificationController {

    private final INotificationGetByUserService getUserNotificationsService;

    @GetMapping
    @SessionRequired(roles = { Role.ROLE_STUDENT, Role.ROLE_TEACHER })
    public ResponseEntity<BaseResponse<List<NotificationResponseDto>>> getUserNotifications(@SessionUser User user) {
        return ResponseFactory.ok(getUserNotificationsService.getUserNotifications(user),
                SuccessfulMessages.okSuccessfully());
    }
}
