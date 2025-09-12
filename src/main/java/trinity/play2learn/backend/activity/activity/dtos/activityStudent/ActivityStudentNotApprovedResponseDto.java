package trinity.play2learn.backend.activity.activity.dtos.activityStudent;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Dificulty;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityStudentNotApprovedResponseDto {
    
    private Long id;
    private String name; 
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Dificulty difficulty;
    private int maxTime;
    private String subjectName;
    private int attempts;
    private int remainingAttempts; //Intentos restantes del estudiante en la actividad
    private ActivityStatus status; //CREATED, PUBLISHED, EXPIRED
    private Double minReward; //Recompensa minima que le entregara la actividad si la realiza correctamente
    private Double maxReward; //Recompensa maxima que le entregara la actividad si la realiza correctamente
    private Boolean pending;
}
