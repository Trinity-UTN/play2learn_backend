package trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.ActivityRequestDto;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdenarSecuenciaRequestDto extends ActivityRequestDto{
    
    @Valid
    @Size(min = 3, max = 10, message = ValidationMessages.LENGTH_EVENTS)
    private List<EventRequestDto> events;
    
}
