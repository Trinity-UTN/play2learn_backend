package trinity.play2learn.backend.activity.activity.mappers;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

public class ActivityCompletedRequestMapper {

    public static ActivityCompletedRequestDto toDto (Long activityId, ActivityCompletedState state) {
        return ActivityCompletedRequestDto.builder()
            .activityId(activityId)
            .state(state)
            .build();
    }
    
}
