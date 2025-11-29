package trinity.play2learn.backend.notification.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.notification.mappers.NotificationMapper;
import trinity.play2learn.backend.notification.models.Notification;
import trinity.play2learn.backend.notification.repositories.INotificationRepository;
import trinity.play2learn.backend.notification.services.commons.NotificationGetByIdService;
import trinity.play2learn.backend.notification.services.interfaces.INotificationMarkAsReadService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class NotificationMarkAsReadService implements INotificationMarkAsReadService {
    
    private final INotificationRepository notificationRepository;
    private final NotificationGetByIdService notificationGetByIdService;
    
    @Override
    @Transactional
    public NotificationResponseDto markAsRead(User user, Long notificationId) {
        
        Notification notification = notificationGetByIdService.getNotificationById(notificationId);
        
        if (!notification.getUser().equals(user)) {
            throw new ConflictException("No puedes marcar como leida una notificacion que no es tuya");
        }

        notification.setRead(true);
        
        return NotificationMapper.toDto(notificationRepository.save(notification));

    }
}
