package trinity.play2learn.backend.activity.arbolDeDecision.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ArbolDeDecisionActivityRequestDto extends ActivityRequestDto{
    
    @NotBlank
    @Size(max = 500, message = ValidationMessages.INTRODUCTION_500_MAX_LENGHT)
    private String introduction;

    @NotNull(message = ValidationMessages.NOT_NULL_DECISION_TREE)
    @Size(min = 2 , max = 2, message = ValidationMessages.OPTIONS_SIZE)
    @Valid
    private List<DecisionArbolDecisionRequestDto> decisionTree;
}
