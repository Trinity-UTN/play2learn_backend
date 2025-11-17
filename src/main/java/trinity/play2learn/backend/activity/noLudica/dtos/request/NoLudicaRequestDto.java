package trinity.play2learn.backend.activity.noLudica.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityRequestDto;
import trinity.play2learn.backend.activity.noLudica.models.TipoEntrega;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
public class NoLudicaRequestDto extends ActivityRequestDto{
    
    @NotNull (message = ValidationMessages.NOT_NULL_EXCERSICE)
    @Size(max = 300, message = ValidationMessages.MAX_LENGTH_EXCERSICE)
    private String excercise;

    @NotNull (message = ValidationMessages.NOT_NULL_TIPO_ENTREGA)
    private TipoEntrega tipoEntrega;
    
}
