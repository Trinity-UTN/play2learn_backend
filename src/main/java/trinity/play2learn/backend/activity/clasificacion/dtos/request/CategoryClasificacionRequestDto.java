package trinity.play2learn.backend.activity.clasificacion.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryClasificacionRequestDto {
    
    @NotBlank
    @Size(max = 50, message = "Maximum length for name is 50 characters.")
    private String name;

    @NotNull
    @Size(min = 1, max = 10,  message = "The category must have between 1 and 10 concepts.")
    @Valid
    private List<ConceptClasificacionRequestDto> concepts; 
}
