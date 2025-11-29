package trinity.play2learn.backend.notification.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.notification.models.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponseDto {
    
    private Long id;
    private String title;
    private NotificationType notificationType;
    private LocalDateTime createdAt;
    private Boolean read;
}
