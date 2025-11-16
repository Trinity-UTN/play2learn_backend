package trinity.play2learn.backend.activity.activity.services.commons.activityStatistics;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStudentsAttemptedCountService;

@Service
@AllArgsConstructor
public class ActivityStudentsAttemptedCountService implements IActivityStudentsAttemptedCountService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    //Obtiene la cantidad de estudiantes que han intentado la actividad
    @Override
    @Transactional(readOnly = true)
    public int getActivityStudentsAttemptedCount(Activity activity) {
        
        List<ActivityCompleted> activityCompleted = activityCompletedRepository.findAllByActivity(activity);

        return (int) activityCompleted.stream().map( a -> a.getStudent().getId()).distinct().count();
    }
    

}
