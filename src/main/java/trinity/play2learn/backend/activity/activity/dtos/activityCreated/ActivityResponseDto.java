package trinity.play2learn.backend.activity.activity.dtos.activityCreated;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.admin.subject.dtos.SubjectSimplifiedResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class ActivityResponseDto {
    
    private Long id;
    private String name; //El nombre sera el nombre de la clase (Ej: Ahorcado, Preguntados, etc)
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Difficulty difficulty;
    private int maxTime;
    private SubjectSimplifiedResponseDto subject;
    private int attempts;
    private Double actualBalance;
    private Double initialBalance;
    private TypeReward typeReward;
}
