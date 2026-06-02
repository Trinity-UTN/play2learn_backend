package trinity.play2learn.backend.notification.services.interfaces;

import trinity.play2learn.backend.notification.models.NotificationType;

public interface INotificationDevGenerateByUserService {

    void generateUserTestNotifications(Long userId, NotificationType notificationType);
}
