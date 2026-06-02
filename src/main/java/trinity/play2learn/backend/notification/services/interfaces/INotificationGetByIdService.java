package trinity.play2learn.backend.notification.services.interfaces;

import trinity.play2learn.backend.notification.models.Notification;

public interface INotificationGetByIdService {
    
    Notification getNotificationById(Long notificationId);
}
