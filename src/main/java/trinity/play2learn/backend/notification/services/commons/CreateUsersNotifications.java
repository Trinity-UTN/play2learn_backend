package trinity.play2learn.backend.notification.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.notification.mappers.NotificationMapper;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.notification.repositories.INotificationRepository;
import trinity.play2learn.backend.notification.services.interfaces.ICreateUsersNotifications;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class CreateUsersNotifications implements ICreateUsersNotifications{
    
    private final INotificationRepository notificationRepository;

    @SuppressWarnings("null")
    @Override
    public void createUsersNotifications(List<User> users, NotificationType notificationType) {
        users.forEach(user -> {
            notificationRepository.save(NotificationMapper.toModel(notificationType, user));
        });
    }
}
