package trinity.play2learn.backend.activity.clasificacion.dtos.request;

import jakarta.validation.constraints.NotBlank;
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
public class ConceptClasificacionRequestDto {
    
    @NotBlank(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH_NAME_100)
    private String name;
    
}
