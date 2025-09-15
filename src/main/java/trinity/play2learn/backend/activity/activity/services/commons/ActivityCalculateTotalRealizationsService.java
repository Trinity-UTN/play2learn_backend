package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateTotalRealizationsService;

@Service
@AllArgsConstructor
public class ActivityCalculateTotalRealizationsService implements IActivityCalculateTotalRealizationsService{

    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public int execute(Activity activity) {
        return activityCompletedRepository.countByActivity(activity);
    }
    
}
