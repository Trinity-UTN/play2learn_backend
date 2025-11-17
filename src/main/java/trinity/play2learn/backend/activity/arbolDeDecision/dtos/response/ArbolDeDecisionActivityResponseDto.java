package trinity.play2learn.backend.activity.arbolDeDecision.dtos.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ArbolDeDecisionActivityResponseDto extends ActivityResponseDto{
    
    private Long id;
    private String introduction;
    private List<DecisionArbolDecisionResponseDto> decisionTree;
}
