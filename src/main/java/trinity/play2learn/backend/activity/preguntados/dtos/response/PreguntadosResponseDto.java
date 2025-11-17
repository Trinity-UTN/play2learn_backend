package trinity.play2learn.backend.activity.preguntados.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
@SuperBuilder
public class PreguntadosResponseDto extends ActivityResponseDto{
    
    private int maxTimePerQuestionInSeconds;
    private List<QuestionResponseDto> questions;
}
