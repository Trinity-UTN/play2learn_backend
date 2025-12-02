package trinity.play2learn.backend.notification.services.commons;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.notification.models.Notification;
import trinity.play2learn.backend.notification.repositories.INotificationRepository;
import trinity.play2learn.backend.notification.services.interfaces.INotificationGetByIdService;

@Service
@AllArgsConstructor
public class NotificationGetByIdService implements INotificationGetByIdService {
    
    private final INotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(() -> new NotFoundException("Notification not found"));
    }
}
