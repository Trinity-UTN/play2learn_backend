package trinity.play2learn.backend.activity.preguntados.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponseDto {
    
    private Long id;
    private String option;
    private Boolean isCorrect;
}
