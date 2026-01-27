package trinity.play2learn.backend.activity.activity.services.commons.noLudica;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNoLudicaStartService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStartService;
import trinity.play2learn.backend.activity.noLudica.models.NoLudica;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityStartNoLudicaService implements IActivityNoLudicaStartService {
    
    private final IActivityStartService activityStartService;
    
    @Override
    public void startNoLudicaActivity(User user, Activity activity) {
        if (activity instanceof NoLudica) {
            activityStartService.execute(user, activity.getId());
        }
    }
}
