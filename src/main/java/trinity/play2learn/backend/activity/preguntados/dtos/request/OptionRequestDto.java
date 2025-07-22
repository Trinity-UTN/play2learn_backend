package trinity.play2learn.backend.activity.preguntados.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionRequestDto {
    
    @NotBlank
    @Size(max = 100, message = "Maximum length for option is 100 characters.")
    private String option;
}
