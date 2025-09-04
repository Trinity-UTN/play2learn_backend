package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetLastCompletedService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityGetLastCompletedService implements IActivityGetLastCompletedService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public ActivityCompleted getLastCompleted(Activity activity, Student student) {

        return activityCompletedRepository.findTopByActivityAndStudentOrderByCompletedAtDesc(activity, student).orElse(null);
    }
    
}
