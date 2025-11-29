package trinity.play2learn.backend.notification.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.notification.services.interfaces.INotificationMarkAsReadService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications/mark-as-read")
public class NotificationMarkAsReadController {
    
    private final INotificationMarkAsReadService notificationMarkAsReadService;

    @PutMapping("/{notificationId}")
    @SessionRequired(roles = {Role.ROLE_STUDENT, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<NotificationResponseDto>> markAsRead(@SessionUser User user, @PathVariable Long notificationId) {

        return ResponseFactory.ok(
            notificationMarkAsReadService.markAsRead(user, notificationId), 
            SuccessfulMessages.updatedSuccessfully("Notificacion"));
    }
}
