package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.user.models.User;

public interface IActivityNoLudicaStartService {
    
    void startNoLudicaActivity(User user, Activity activity);
}
