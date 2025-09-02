package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class ActivityGetByIdService implements IActivityGetByIdService{
    
    private final IActivityRepository activityRepository;

    @Override
    public Activity findActivityById(Long activityId) {
        
        
        return activityRepository.findById(activityId).orElseThrow(
            () -> new NotFoundException("Activity with id " + activityId + " not found")
        );
    }
    
}
