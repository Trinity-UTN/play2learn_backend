package trinity.play2learn.backend.activity.arbolDeDecision.services.interfaces;

import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IArbolDecisionGenerateService {
    
    ArbolDeDecisionActivityResponseDto cu46GenerateArbolDeDecisionActivity(ArbolDeDecisionActivityRequestDto activityDto, User user);
}
