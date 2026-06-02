package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class ActivityGetCompletedByIdService implements IActivityGetCompletedByIdService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public ActivityCompleted findActivityCompletedById(Long activityCompletedId) {
        return activityCompletedRepository.findById(activityCompletedId).orElseThrow(
            () -> new NotFoundException("ActivityCompleted with id " + activityCompletedId + " not found")
        );
    }
}
