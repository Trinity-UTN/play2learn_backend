package trinity.play2learn.backend.activity.preguntados.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuestionResponseDto {
    
    
    private String question;
    private List<OptionResponseDto> options;
}
