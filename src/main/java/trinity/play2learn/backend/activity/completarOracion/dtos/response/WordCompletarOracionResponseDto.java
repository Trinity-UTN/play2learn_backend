package trinity.play2learn.backend.activity.completarOracion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordCompletarOracionResponseDto {
    
    private Long id;
    private String word;
    private int order;
    private Boolean isMissing;
}
