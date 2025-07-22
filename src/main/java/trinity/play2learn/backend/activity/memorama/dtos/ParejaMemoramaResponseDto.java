package trinity.play2learn.backend.activity.memorama.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParejaMemoramaResponseDto {

    private Long id;
    private String concepto;
    private String imagen;
    
}
