package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;

public interface IActivityGetService {
    
    ActivityResponseDto cu64GetActivity(Long id);
}
