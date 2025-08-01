package trinity.play2learn.backend.activity.completarOracion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class WordCompletarOracionRequestDto {
    
    @NotBlank (message = ValidationMessages.NOT_EMPTY_WORD)
    @Size(min = 1, max = 30, message = ValidationMessages.LENGTH_WORD)
    private String word;

    @NotNull (message = ValidationMessages.NOT_NULL_WORD_ORDER)
    private Integer wordOrder; 
    //Este atributo permite al front enviar las palabras desordenadas. No se guardara en el back, sino que se ordenara el array de words y se guardara ordenado.
    
    @NotNull (message = ValidationMessages.NOT_NULL_IS_MISSING)
    private Boolean isMissing;
}
