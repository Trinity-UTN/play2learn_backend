package trinity.play2learn.backend.activity.preguntados.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.ActivityRequestDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class PreguntadosRequestDto extends ActivityRequestDto{

    @Min(10)
    private int maxTimePerQuestionInSeconds; 

    @Valid
    @Size(min = 5, message = "Must have at least 5 questions")
    private List<QuestionRequestDto> questions; //Preguntas

}
