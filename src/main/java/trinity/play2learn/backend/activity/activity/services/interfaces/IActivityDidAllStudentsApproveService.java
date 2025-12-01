package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public interface IActivityDidAllStudentsApproveService {
    
    boolean didAllStudentsApprove(Activity activity);
}
