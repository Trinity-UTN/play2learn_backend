package trinity.play2learn.backend.notification.services.interfaces;

import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.user.models.User;

public interface INotificationCreateSingleWithTitleService {
    
    void createSingleNotificationWithTitle(User user, NotificationType notificationType, String title);
}
