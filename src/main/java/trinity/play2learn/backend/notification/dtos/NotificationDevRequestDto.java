package trinity.play2learn.backend.notification.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.notification.models.NotificationType;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NotificationDevRequestDto {
    
    @NotNull
    private Long userId;
    @NotNull
    private NotificationType notificationType;
}
