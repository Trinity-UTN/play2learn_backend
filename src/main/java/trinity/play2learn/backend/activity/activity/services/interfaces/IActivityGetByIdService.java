package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public interface IActivityGetByIdService {
    
    Activity findActivityById(Long activityId);
}
