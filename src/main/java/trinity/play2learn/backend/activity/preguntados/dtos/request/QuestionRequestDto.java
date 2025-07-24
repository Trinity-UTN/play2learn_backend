package trinity.play2learn.backend.activity.preguntados.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionRequestDto {

    @NotBlank(message = "Question is required")
    @Size(max = 200, message = "Maximum length for question is 200 characters.")
    private String question;

    @Valid
    @Size(min = 4, max = 4, message = "Must have 4 options")
    private List<OptionRequestDto> options; //Maximo de 100 caracteres por opcion
    //Cree un dto de Options para poder validar restricciones dentro de cada elemento de la lista 
}
