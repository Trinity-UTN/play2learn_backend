package trinity.play2learn.backend.activity.activity.services.commons.activityStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterByDisapprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.admin.student.models.Student;

@AllArgsConstructor
@Service
public class ActivityCountAvailableByStudentService{

    private final IActivityGetByStudentService activityGetByStudentService;

    private final IActivityFilterApprovedService activityFilterApprovedService;

    private final IActivityFilterByDisapprovedService activityFilterByDisapprovedService;
    private final IActivityGetCompletedStateService activityGetCompletedStateService;
    
    //Obtiene la cantidad de actividades que estan disponibles para realizar por un estudiante
    public int countAvailableByStudent(Student student){
        
        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        activities = activities.stream()
        .filter(Activity::isPublished)
        .collect(Collectors.toCollection(ArrayList::new));

        List<Activity> approvedActivities = activityFilterApprovedService.filterByApproved(activities, student);

        List<Activity> disapprovedActivities = activityFilterByDisapprovedService.filterByDisapproved(activities, student, true);

        List<Activity> pendingActivities = activities.stream()
            .filter(activity -> activityGetCompletedStateService.getActivityCompletedState(activity, student) == ActivityCompletedState.PENDING)
            .collect(Collectors.toCollection(ArrayList::new));

        activities.removeAll(approvedActivities);
        activities.removeAll(disapprovedActivities);
        activities.removeAll(pendingActivities);

        return activities.size();
    }
}
