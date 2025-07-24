package trinity.play2learn.backend.activity.completarOracion.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceCompletarOracionRequestDto {

    @Valid
    @Size(min = 1 , max = 300, message = "The sentence must have between 1 and 300 words.")
    private List<WordCompletarOracionRequestDto> words; 
}
