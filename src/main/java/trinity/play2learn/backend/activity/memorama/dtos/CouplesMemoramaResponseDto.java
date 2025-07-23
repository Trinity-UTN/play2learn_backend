package trinity.play2learn.backend.activity.memorama.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouplesMemoramaResponseDto {

    private Long id;
    private String concept;
    private String image;
    
}
