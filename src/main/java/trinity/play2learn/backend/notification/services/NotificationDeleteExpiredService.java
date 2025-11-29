package trinity.play2learn.backend.notification.services;

import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.notification.repositories.INotificationRepository;
import trinity.play2learn.backend.notification.services.interfaces.INotificationDeleteExpiredService;

@Service
@AllArgsConstructor
public class NotificationDeleteExpiredService implements INotificationDeleteExpiredService {

    private final INotificationRepository notificationRepository;

    // Elimina todas las notificaciones que han expirado
    @Override
    @Scheduled(cron = "0 30 1 * * ?") // Cada dia a las 1:30
    @Transactional
    public void cu123deleteExpiredNotifications() {
        notificationRepository.deleteAllByExpiredAtBefore(LocalDateTime.now());
    }
}
