package trinity.play2learn.backend.activity.clasificacion.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryClasificacionResponseDto {
    
    private Long id;
    private String name;
    private List<ConceptClasificacionResponseDto> concepts;
}
