package trinity.play2learn.backend.activity.activity.dtos.activityStudent;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

@Data
@Builder
@AllArgsConstructor
public class ActivityStudentApprovedResponseDto {
    
    private Long id;
    private String name; 
    private String description;
    private Difficulty difficulty;
    private String subjectName;
    private int attempts;
    private Integer remainingAttempts; //Intentos restantes del estudiante en la actividad
    private LocalDateTime completedAt;
    private Double reward;
    private ActivityCompletedState state; //APPROVE
}

