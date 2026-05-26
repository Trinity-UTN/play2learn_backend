package trinity.play2learn.backend.activity.activity.dtos.activityCreated;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class ActivityRequestDto {
    
    @Size(max = 1000, message = ValidationMessages.MAX_LENGTH_DESCRIPTION_1000)
    private String description;

    private LocalDateTime startDate; //En caso de llegar null, se setea como la fecha y hora actual

    @NotNull (message = ValidationMessages.NOT_NULL_END_DATE)
    private LocalDateTime endDate;

    @NotNull (message = ValidationMessages.NOT_NULL_DIFFICULTY)
    private Difficulty difficulty;

    private int maxTime; //En minutos

    @NotNull (message = ValidationMessages.NOT_NULL_SUBJECT)
    private Long subjectId;

    private int attempts;

    private Double initialBalance;

    private TypeReward typeReward;
}
