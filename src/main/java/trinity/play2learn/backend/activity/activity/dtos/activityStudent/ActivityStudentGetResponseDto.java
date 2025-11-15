package trinity.play2learn.backend.activity.activity.dtos.activityStudent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityStudentGetResponseDto {
    
    private String studentName;
    private ActivityCompletedState state; // No realizada, aprobada, desaprobada, en curso
    private int attempts; //Intentos realizados
    private Double reward; //Recompensa que obtuvo
}
