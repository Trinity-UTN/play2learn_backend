package trinity.play2learn.backend.activity.arbolDeDecision.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsecuenceArbolDecisionResponseDto {
    
    private Long id;
    private String name;
    private boolean approvesActivity;
}
