package trinity.play2learn.backend.activity.activity.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.Dificulty;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;

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
    private Dificulty dificulty;
    private int maxTime;
    private SubjectResponseDto subject;
}
