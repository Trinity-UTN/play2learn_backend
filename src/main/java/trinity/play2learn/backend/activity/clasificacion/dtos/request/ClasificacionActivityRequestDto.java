package trinity.play2learn.backend.activity.clasificacion.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
public class ClasificacionActivityRequestDto extends ActivityRequestDto {
    
    @NotNull(message = ValidationMessages.NOT_NULL_CATEGORIES)
    @Size(min = 2, max = 10,  message = ValidationMessages.LENGTH_CATEGORIES)
    @Valid
    private List<CategoryClasificacionRequestDto> categories;
}
