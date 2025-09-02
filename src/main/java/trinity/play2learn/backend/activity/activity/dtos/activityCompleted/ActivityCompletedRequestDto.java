package trinity.play2learn.backend.activity.activity.dtos.activityCompleted;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@AllArgsConstructor
@Builder
public class ActivityCompletedRequestDto {
    
    @NotNull(message = ValidationMessages.NOT_NULL_ACTIVITY_ID)
    private Long activityId;

    @NotNull(message = ValidationMessages.NOT_NULL_STATE)
    private ActivityCompletedState state; //APPROVED, DISAPPROVED, PENDING
}
