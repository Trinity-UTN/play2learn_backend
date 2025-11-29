package trinity.play2learn.backend.notification.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import trinity.play2learn.backend.notification.models.Notification;
import trinity.play2learn.backend.user.models.User;

public interface INotificationRepository extends CrudRepository<Notification, Long> {
    
    List<Notification> findAllByUserAndExpiredAtAfter(User user, LocalDateTime now);

    void deleteAllByExpiredAtBefore(LocalDateTime now);
}       
