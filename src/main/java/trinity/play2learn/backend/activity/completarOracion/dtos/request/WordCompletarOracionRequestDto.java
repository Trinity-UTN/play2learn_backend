package trinity.play2learn.backend.activity.completarOracion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordCompletarOracionRequestDto {
    
    @NotBlank
    @Size(min = 1, max = 30, message = "The word must be between 1 and 30 characters.")
    private String word;

    @NotNull
    private Integer wordOrder; 
    //Este atributo permite al front enviar las palabras desordenadas. No se guardara en el back, sino que se ordenara el array de words y se guardara ordenado.
    
    @NotNull
    private Boolean isMissing;
}
