package trinity.play2learn.backend.activity.ahorcado.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AhorcadoResponseDto extends ActivityResponseDto {

    private String word;
    private String errorsPermited; //Se devolvera el nombre del enum Errors (TRES o CINCO)

    private int errorsPermitedValue; //Se devolvera el valor del enum Errors (3 o 5)


}
