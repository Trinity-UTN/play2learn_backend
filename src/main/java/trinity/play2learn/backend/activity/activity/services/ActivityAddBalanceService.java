package trinity.play2learn.backend.activity.activity.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityAddBalanceService;

@Service
@AllArgsConstructor
public class ActivityAddBalanceService implements IActivityAddBalanceService {

    private final IActivityRepository activityRepository;

    @Override
    public void execute(Activity activity, Double amount) {
        
        activity.setActualBalance(activity.getActualBalance() + amount);
        
        activityRepository.save(activity);

    }
    
}
