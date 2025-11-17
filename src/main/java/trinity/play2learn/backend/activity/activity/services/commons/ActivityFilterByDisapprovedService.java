package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterByDisapprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetLastCompletedService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityFilterByDisapprovedService implements IActivityFilterByDisapprovedService {
    
    private final IActivityGetLastCompletedService activityGetLastCompletedService;

    //Filtra las actividades por desaprobadas o no desaprobadas segun el booleano.
    @Override
    public List<Activity> filterByDisapproved(List<Activity> activities, Student student, Boolean disapproved) {

        return activities.stream()

                .filter(activity -> {
                    var lastCompleted = activityGetLastCompletedService.getLastCompleted(activity, student);

                    if (lastCompleted == null) {
                        return !disapproved; //Si no hay intentos realizados, devuelve las no desaprobadas
                    }

                    return disapproved 
                        ? lastCompleted.getRemainingAttempts() == 0   // desaprobadas
                        : lastCompleted.getRemainingAttempts() > 0;   // no desaprobadas
                })
                .toList();
    }
    
    
}
