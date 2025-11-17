package trinity.play2learn.backend.activity.activity.mappers;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public interface IActivityMapper {
    
    ActivityResponseDto toActivityDto(Activity activity);
}
