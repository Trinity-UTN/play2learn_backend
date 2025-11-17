package trinity.play2learn.backend.activity.completarOracion.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceCompletarOracionRequestDto {

    @Valid
    @Size(min = 3 , max = 300, message = ValidationMessages.LENGTH_SENTENCE)
    private List<WordCompletarOracionRequestDto> words; 
}
