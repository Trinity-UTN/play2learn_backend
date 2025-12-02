package trinity.play2learn.backend.notification.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface INotificationGetByUserService {

    List<NotificationResponseDto> getUserNotifications(User user);
}
