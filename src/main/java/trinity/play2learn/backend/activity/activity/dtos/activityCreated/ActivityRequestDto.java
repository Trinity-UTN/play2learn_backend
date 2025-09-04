package trinity.play2learn.backend.activity.activity.dtos.activityCreated;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.activity.Dificulty;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class ActivityRequestDto {
    
    @Size(max = 1000, message = ValidationMessages.MAX_LENGTH_DESCRIPTION_1000)
    private String description;

    @NotNull (message = ValidationMessages.NOT_NULL_START_DATE)
    private LocalDateTime startDate;

    @NotNull (message = ValidationMessages.NOT_NULL_END_DATE)
    private LocalDateTime endDate;

    @NotNull (message = ValidationMessages.NOT_NULL_DIFICULTY)
    private Dificulty dificulty;

    private int maxTime; //En minutos

    @NotNull (message = ValidationMessages.NOT_NULL_SUBJECT)
    private Long subjectId;

    private int attempts;

    private Double initialBalance;

    private TypeReward typeReward;
}
