package trinity.play2learn.backend.notification.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.notification.mappers.NotificationMapper;
import trinity.play2learn.backend.notification.models.Notification;
import trinity.play2learn.backend.notification.repositories.INotificationRepository;
import trinity.play2learn.backend.notification.services.interfaces.INotificationGetByUserService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class NotificationGetByUserService implements INotificationGetByUserService {

        private final INotificationRepository notificationRepository;

        @Override
        @Transactional(readOnly = true)
        public List<NotificationResponseDto> getUserNotifications(User user) {

                List<Notification> notifications = notificationRepository.findAllByUserAndExpiredAtAfter(user,LocalDateTime.now());

                notifications = notifications
                                .stream()
                                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed()) // 1. MÁS NUEVAS primero (Descendente)
                                .sorted(Comparator.comparing(Notification::isRead)) // 2. NO LEÍDAS (false) antes que LEÍDAS (true) (Ascendente natural)
                                .toList();

                return notifications.stream().map(NotificationMapper::toDto).toList();
        }
}
