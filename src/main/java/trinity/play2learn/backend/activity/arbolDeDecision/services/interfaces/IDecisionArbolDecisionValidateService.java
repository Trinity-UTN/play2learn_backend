package trinity.play2learn.backend.activity.arbolDeDecision.services.interfaces;

import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.DecisionArbolDecisionRequestDto;

public interface IDecisionArbolDecisionValidateService {

    void validateDecisionAndOptions(DecisionArbolDecisionRequestDto decisionDto);
}
