package trinity.play2learn.backend.activity.clasificacion.dtos.response;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ClasificacionActivityResponseDto extends ActivityResponseDto {
    
    @NotNull
    @Size(min = 2, max = 10,  message = "The activity must have between 2 and 10 categories.")
    private List<CategoryClasificacionResponseDto> categories; 
}
