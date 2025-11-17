package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountByStudent;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCountByStudent implements IActivityCountByStudent{

    private final IActivityGetByStudentService activityGetByStudentService;

    private final IActivityFilterApprovedService activityFilterApprovedService;

    @Override
    public int[] execute(Student student) {

        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        List<Activity> availableActivities = activities.stream().filter(a -> a.isAvailable()).toList();

        int totalActivities = availableActivities.size(); //Cantidad de actividades disponibles

        int totalCompletedActivities = activityFilterApprovedService.filterByApproved(availableActivities, student).size(); 
        //Filtra las aprobadas de las disponibles y las cuenta
 
        return new int[]{totalActivities, totalCompletedActivities};
    }
    
}
