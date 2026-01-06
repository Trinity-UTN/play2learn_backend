package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.noLudica.NoLudicaAttemptResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityGetNoLudicaAttemptService {
    
    NoLudicaAttemptResponseDto cu127GetNoLudicaAttempt(Long activityCompletedId, User user);

}
