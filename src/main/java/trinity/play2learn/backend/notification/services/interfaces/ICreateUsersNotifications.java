package trinity.play2learn.backend.notification.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.user.models.User;

public interface ICreateUsersNotifications {
    
    void createUsersNotifications(List<User> users, NotificationType notificationType);
}
