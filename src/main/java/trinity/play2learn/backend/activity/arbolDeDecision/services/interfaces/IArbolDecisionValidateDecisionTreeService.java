package trinity.play2learn.backend.activity.arbolDeDecision.services.interfaces;

import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;

public interface IArbolDecisionValidateDecisionTreeService {
    
    void validateDecisionTree(ArbolDeDecisionActivityRequestDto arbolDecisionDto);
}
