package trinity.play2learn.backend.activity.arbolDeDecision.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DecisionArbolDecisionResponseDto {
    
    private Long id;
    private String name;
    private List<DecisionArbolDecisionResponseDto> options;
    private ConsecuenceArbolDecisionResponseDto consecuence;
}
