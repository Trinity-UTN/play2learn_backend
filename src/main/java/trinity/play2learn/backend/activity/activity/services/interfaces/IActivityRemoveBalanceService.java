package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.Activity;

public interface IActivityRemoveBalanceService {
    
    public void execute (Activity activity, Double amount);
    
}
