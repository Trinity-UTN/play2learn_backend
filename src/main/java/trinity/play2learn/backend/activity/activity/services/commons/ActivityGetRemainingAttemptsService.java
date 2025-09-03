package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityGetRemainingAttemptsService implements IActivityGetRemainingAttemptsService{

    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public Integer getStudentRemainingAttempts(Activity activity, Student student) {

        Optional<ActivityCompleted> activityCompleted = activityCompletedRepository.findTopByActivityAndStudentOrderByCompletedAtDesc(activity, student);

        if (activityCompleted.isPresent()) {
            return activityCompleted.get().getRemainingAttempts();
        } else {

            return activity.getAttempts();
            
        }
    }
    
}
