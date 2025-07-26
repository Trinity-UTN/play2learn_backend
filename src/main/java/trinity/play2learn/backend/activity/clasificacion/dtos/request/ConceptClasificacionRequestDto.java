package trinity.play2learn.backend.activity.clasificacion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConceptClasificacionRequestDto {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Maximum length for name is 100 characters.")
    private String name;
    
}
