package trinity.play2learn.backend.activity.activity.services.commons.activityStatistics;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityAverageCompletionTimeService;

@Service
@AllArgsConstructor
public class ActivityAverageCompletionTimeService implements IActivityAverageCompletionTimeService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public Double activityGetAverageCompletionTime(Activity activity) {
        
        return activityCompletedRepository.findAllByActivity(activity)
            .stream()
            .filter(a -> a.getCompletedAt() != null) //Filtra los que no han sido completados para que no afecten a la media
            .mapToInt(ActivityCompleted::getCompletionTime)
            .average()
            .orElse(0);
    }
    
    
}
