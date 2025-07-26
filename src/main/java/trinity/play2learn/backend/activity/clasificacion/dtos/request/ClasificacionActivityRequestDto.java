package trinity.play2learn.backend.activity.clasificacion.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import trinity.play2learn.backend.activity.activity.dtos.ActivityRequestDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
public class ClasificacionActivityRequestDto extends ActivityRequestDto {
    
    @NotNull(message = "Categories is required")
    @Size(min = 2, max = 10,  message = "The activity must have between 2 and 10 categories.")
    @Valid
    private List<CategoryClasificacionRequestDto> categories;
}
