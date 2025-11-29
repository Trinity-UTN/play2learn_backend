package trinity.play2learn.backend.notification.services.interfaces;

import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface INotificationMarkAsReadService {
    
    NotificationResponseDto markAsRead(User user, Long notificationId);
}
