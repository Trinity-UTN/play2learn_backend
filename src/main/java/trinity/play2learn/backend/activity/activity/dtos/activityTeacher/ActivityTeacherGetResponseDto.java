package trinity.play2learn.backend.activity.activity.dtos.activityTeacher;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentGetResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityTeacherGetResponseDto {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ActivityStatus status; // CREATED, PUBLISHED, EXPIRED
    private Difficulty difficulty;
    private int maxTime;
    private String subjectName;
    private String courseName;
    private int attempts;
    private Double reward; //Recompensa que entregara a cada estudiante (En el caso de Poisson, seria en ese momento, ya que cambia segun los estudiantes que la realicen)
    private TypeReward typeReward;

    private int studentsAttemptedCount; //Cantidad de estudiantes que realizaron la actividad;
    private int studentsApprovedCount; // Cantidad de estudiantes que aprobaron la actividad;
    private Double participationPercentage; // Porcentaje de participación;
    private Double averageCompletionTime; // Tiempos promedios de realización;
    private Double successPercentage; // Porcentaje de éxito (Estudiantes que aprobaron /Estudiantes que realizaron al menos un intento).

    private List<ActivityStudentGetResponseDto> activityStudentGetDtos; 
}
