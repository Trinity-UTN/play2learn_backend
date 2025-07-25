package trinity.play2learn.backend.activity.clasificacion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConceptClasificacionResponseDto {
    
    private Long id;
    private String name;
}
