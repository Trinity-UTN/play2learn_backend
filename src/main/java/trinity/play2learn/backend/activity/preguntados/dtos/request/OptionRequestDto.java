package trinity.play2learn.backend.activity.preguntados.dtos.request;

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
public class OptionRequestDto {
    
    @NotBlank (message = ValidationMessages.NOT_EMPTY_OPTION)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH_OPTION)
    private String option;

    private Boolean isCorrect;
}
