package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountByStudent;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCountByStudent implements IActivityCountByStudent{

    private final IActivityGetByStudentService activityGetByStudentService;

    private final IActivityGetCompletedStateService activityGetCompletedStateService;
    
    @Override
    public int[] execute(Student student) {

        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        int totalActivities = activities.size();

        int totalCompletedActivities = 0;

        for (Activity activity : activities) {
            if (activityGetCompletedStateService.getActivityCompletedState(activity, student) != null) {
                totalCompletedActivities++;
            }
        } 

        return new int[]{totalActivities, totalCompletedActivities};
    }
    
}
