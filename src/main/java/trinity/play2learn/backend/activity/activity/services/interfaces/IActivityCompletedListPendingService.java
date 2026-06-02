package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedPendingDto;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;

public interface IActivityCompletedListPendingService {
    
    List<ActivityCompletedPendingDto> cu128ListPendingActivities(User user);

    PaginatedData<ActivityCompletedPendingDto> cu128ListPendingActivitiesPaginated(
        int page,
        int size, 
        String orderBy, 
        String orderType, 
        String search, 
        List<String> filters,
        List<String> filterValues,
        User user
    );

}