package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityGetCompletedStateService implements IActivityGetCompletedStateService {
    
    private final IActivityCompletedRepository activityCompletedRepository;
    
    //Devuelve APPROVE, DISAPPROVED, PENDING o Null si no la actividad nunca se realizo.
    @Override
    public ActivityCompletedState getActivityCompletedState(Activity activity, Student student) {

        Optional<ActivityCompleted> activityCompleted = activityCompletedRepository.findTopByActivityAndStudentOrderByCompletedAtDesc(activity, student);

        if (activityCompleted.isEmpty()) {

            return null; 
        } 

        return activityCompleted.get().getState();

    }
    
    
}
