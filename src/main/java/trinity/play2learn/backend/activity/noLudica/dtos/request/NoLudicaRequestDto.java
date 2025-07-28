package trinity.play2learn.backend.activity.noLudica.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import trinity.play2learn.backend.activity.activity.dtos.ActivityRequestDto;
import trinity.play2learn.backend.activity.noLudica.models.TipoEntrega;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
public class NoLudicaRequestDto extends ActivityRequestDto{
    
    @NotNull
    @Size(max = 300, message = "Maximum length for excercise is 300 characters.")
    private String excercise;

    @NotNull
    private TipoEntrega tipoEntrega;
    
}
