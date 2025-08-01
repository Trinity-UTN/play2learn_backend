package trinity.play2learn.backend.activity.ahorcado.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import trinity.play2learn.backend.activity.activity.dtos.ActivityRequestDto;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
public class AhorcadoRequestDto extends ActivityRequestDto {
    
    @NotEmpty(message = ValidationMessages.NOT_EMPTY_WORD)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_WORD)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = ValidationMessages.PATTERN_WORD)
    private String word;

    @NotNull(message = ValidationMessages.NOT_NULL_ERRORS_PERMITED)
    private Errors errorsPermited; //TRES o CINCO
}
