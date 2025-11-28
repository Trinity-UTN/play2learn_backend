package trinity.play2learn.backend.notification.mappers;

import trinity.play2learn.backend.notification.models.Notification;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.user.models.User;

public class NotificationMapper {
    
    public static Notification toModel(NotificationType notificationType, User user){
        return Notification.builder()
            .title(notificationType.getTitle())
            .type(notificationType)
            .read(false)
            .user(user)
            .build();
    }
}
