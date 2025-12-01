package trinity.play2learn.backend.notification.services.commons;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.notification.repositories.INotificationRepository;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCreateSingleWithTitleService;
import trinity.play2learn.backend.notification.mappers.NotificationMapper;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class NotificationCreateSingleWithTitleService implements INotificationCreateSingleWithTitleService{
    
    private final INotificationRepository notificationRepository;

    @Override
    @Transactional
    public void createSingleNotificationWithTitle(User user, NotificationType notificationType, String title) {
        notificationRepository.save(NotificationMapper.toModel(notificationType, user, title));
    }
}
