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

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
public class AhorcadoRequestDto extends ActivityRequestDto {
    
    @NotEmpty(message = "Word is required.")
    @Size(max = 50, message = "Maximum length for word is 50 characters.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Word can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
    private String word;

    @NotNull(message = "ErrorsPermited is required.")
    private Errors errorsPermited; //TRES o CINCO
}
