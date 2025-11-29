package trinity.play2learn.backend.notification.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCreateByUsersService;
import trinity.play2learn.backend.notification.services.interfaces.INotificationDevGenerateByUserService;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;

@Service
@AllArgsConstructor
public class NotificationDevGenerateByUserService implements INotificationDevGenerateByUserService {

    private final INotificationCreateByUsersService createUsersNotifications;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public void generateUserTestNotifications(Long userId, NotificationType notificationType) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<User> users = List.of(user);
        createUsersNotifications.createUsersNotifications(users, notificationType);
    }
}
