package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityFilterApprovedService implements IActivityFilterApprovedService{
    
    private final IActivityGetCompletedStateService activityGetCompletedStateService;
    
    @Override
    public List<Activity> filterByApproved(List<Activity> activities, Student student) {

        return activities
            .stream()
            .filter(activity -> activityGetCompletedStateService.getActivityCompletedState(activity, student) == ActivityCompletedState.APPROVED)
            .toList();
    }
}
