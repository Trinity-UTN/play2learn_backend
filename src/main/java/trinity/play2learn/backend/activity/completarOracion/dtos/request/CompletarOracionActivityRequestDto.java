package trinity.play2learn.backend.activity.completarOracion.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.ActivityRequestDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CompletarOracionActivityRequestDto extends ActivityRequestDto{
    
    @Valid
    @Size(min = 1, max = 20, message = "The activity must have between 1 and 20 sentences.")
    private List<SentenceCompletarOracionRequestDto> sentences;
    
}
