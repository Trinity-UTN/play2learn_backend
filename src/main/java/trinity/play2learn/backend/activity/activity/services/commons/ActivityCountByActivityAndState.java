package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountByActivityAndState;

@Service
@AllArgsConstructor
public class ActivityCountByActivityAndState implements IActivityCountByActivityAndState{ 

    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public int execute(Activity activity, ActivityCompletedState state) {
        return activityCompletedRepository.countByActivityAndState(activity, state);
    }

}
