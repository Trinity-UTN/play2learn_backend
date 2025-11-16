package trinity.play2learn.backend.activity.activity.services.commons.activityStatistics;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStudentsApprovedCountService;

@Service
@AllArgsConstructor
public class ActivityStudentsApprovedCountService implements IActivityStudentsApprovedCountService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    @Transactional(readOnly = true)
    public int activityGetStudentsApprovedCount(Activity activity) {
        
        return activityCompletedRepository.countByActivityAndState(activity, ActivityCompletedState.APPROVED);
    }
    
    
}
