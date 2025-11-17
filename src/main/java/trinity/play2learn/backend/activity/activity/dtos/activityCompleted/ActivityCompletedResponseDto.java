package trinity.play2learn.backend.activity.activity.dtos.activityCompleted;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

@Data
@AllArgsConstructor
@Builder
public class ActivityCompletedResponseDto {
    
    private Long id;
    private Long activityId;
    private ActivityCompletedState state; //APPROVED, DISAPPROVED, PENDING
    private Double reward; //Null si no se ha aprobado la actividad
    private Integer remainingAttempts; //Null si no se ha desaprobado la actividad 
}
