package trinity.play2learn.backend.activity.arbolDeDecision.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsecuenceArbolDecisionRequestDto {
    
    @NotBlank(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 200, message = ValidationMessages.NAME_200_MAX_LENGHT)
    private String name;

    private boolean approvesActivity;
}
