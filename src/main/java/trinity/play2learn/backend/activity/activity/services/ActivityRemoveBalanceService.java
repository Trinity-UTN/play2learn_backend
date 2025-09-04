package trinity.play2learn.backend.activity.activity.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityRemoveBalanceService;

@Service
@AllArgsConstructor
public class ActivityRemoveBalanceService implements IActivityRemoveBalanceService{

    private final IActivityRepository activityRepository;

    @Override
    public void execute(Activity activity, Double amount) {
        
        activity.setActualBalance(activity.getActualBalance() - amount);
        
        activityRepository.save(activity);
    }
    
}
