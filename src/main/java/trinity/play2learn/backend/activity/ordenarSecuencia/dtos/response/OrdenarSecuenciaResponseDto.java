package trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.ActivityResponseDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OrdenarSecuenciaResponseDto extends ActivityResponseDto{
    
    private List<EventResponseDto> events;
    
}
