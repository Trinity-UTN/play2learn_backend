package trinity.play2learn.backend.notification.services.interfaces;

import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface INotificationMarkAsReadService {

    NotificationResponseDto cu118markAsRead(User user, Long notificationId);
}
