package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCompletedGetLastStartedService implements IActivityCompletedGetLastStartedService{
    
    private final IActivityCompletedRepository activityCompletedRepository;
    
    @Override
    public Optional<ActivityCompleted> get(Activity activity, Student student) {
        return activityCompletedRepository.findTopByActivityAndStudentAndStateOrderByStartedAtDesc(activity, student, trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState.IN_PROGRESS);
    }
    

}
