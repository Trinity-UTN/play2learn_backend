package trinity.play2learn.backend.activity.activity.services.commons.filter;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterNotApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityFilterNotApprovedService implements IActivityFilterNotApprovedService {
    
    private final IActivityGetCompletedStateService activityGetCompletedStateService;

    //Filtra las actividades no aprobadas y no pendientes
    @Override
    public List<Activity> filterByNotApproved(List<Activity> activities, Student student) {

        return activities
            .stream()
            .filter(activity -> {

                ActivityCompletedState state = activityGetCompletedStateService.getActivityCompletedState(activity, student);
                return state != ActivityCompletedState.APPROVED && state != ActivityCompletedState.PENDING;
            })
            .toList();
    }
    
    
}
