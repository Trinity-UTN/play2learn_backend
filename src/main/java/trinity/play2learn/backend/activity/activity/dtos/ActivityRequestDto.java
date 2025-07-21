package trinity.play2learn.backend.activity.activity.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.Dificulty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class ActivityRequestDto {
    
    @Size(max = 1000, message = "Maximum length for description is 1000 characters.")
    private String description;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private Dificulty dificulty;

    private int maxTime; //En minutos

    @NotNull
    private Long subjectId;

    private int attempts;
}
