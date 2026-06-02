package trinity.play2learn.backend.notification.mappers;

import java.util.List;

import trinity.play2learn.backend.notification.dtos.NotificationResponseDto;
import trinity.play2learn.backend.notification.models.Notification;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.user.models.User;

public class NotificationMapper {
    
    public static Notification toModel(NotificationType notificationType, User user, String title){
        return Notification.builder()
            .title(title != null ? title : notificationType.getTitle())
            .type(notificationType)
            .read(false)
            .user(user)
            .build();
    }

    public static NotificationResponseDto toDto(Notification notification){
        return NotificationResponseDto.builder()
            .id(notification.getId())
            .title(notification.getTitle())
            .notificationType(notification.getType())
            .createdAt(notification.getCreatedAt())
            .read(notification.isRead())
            .build();
    }

    public static List<NotificationResponseDto> toDtoList(List<Notification> notifications){
        return notifications
            .stream()
            .map(NotificationMapper::toDto)
            .toList();
    }
}
