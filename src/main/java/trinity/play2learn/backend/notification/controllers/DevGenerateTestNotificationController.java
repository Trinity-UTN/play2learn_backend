package trinity.play2learn.backend.notification.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.notification.dtos.NotificationDevRequestDto;
import trinity.play2learn.backend.notification.services.NotificationDevGenerateByUserService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications/dev/generate-user-notifications")
public class DevGenerateTestNotificationController {

    private final NotificationDevGenerateByUserService devGenerateTestNotificationsService;

    @PostMapping
    @SessionRequired(roles = { Role.ROLE_DEV })
    public void generateUserNotifications(@RequestBody NotificationDevRequestDto notificationDevRequestDto) {
        devGenerateTestNotificationsService.generateUserTestNotifications(notificationDevRequestDto.getUserId(),
                notificationDevRequestDto.getNotificationType());
    }
}
