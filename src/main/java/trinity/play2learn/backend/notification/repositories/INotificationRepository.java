package trinity.play2learn.backend.notification.repositories;

import org.springframework.data.repository.CrudRepository;
import trinity.play2learn.backend.notification.models.Notification;

public interface INotificationRepository extends CrudRepository<Notification, Long> {
    
}
