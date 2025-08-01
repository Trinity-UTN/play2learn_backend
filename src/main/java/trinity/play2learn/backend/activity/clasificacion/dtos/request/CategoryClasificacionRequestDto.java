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
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryClasificacionRequestDto {
    
    @NotBlank (message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_NAME)
    private String name;

    @NotNull (message = ValidationMessages.NOT_NULL_CONCEPTS)
    @Size(min = 1, max = 10,  message = ValidationMessages.LENGTH_CONCEPTS)
    @Valid
    private List<ConceptClasificacionRequestDto> concepts; 
}
