package trinity.play2learn.backend.activity.activity.services.commons;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStatusService;

@Service
@AllArgsConstructor
public class ActivityGetStatusService implements IActivityGetStatusService {
    
    @Override
    public ActivityStatus getStatus(Activity activity) {
        
        if (activity.getStartDate().isAfter(LocalDateTime.now())) {
            return ActivityStatus.CREATED;
            
        } else if (activity.getEndDate().isBefore(LocalDateTime.now())) {
            return ActivityStatus.EXPIRED;
        } else {
            return ActivityStatus.PUBLISHED;
        }
    }
    
    
}
