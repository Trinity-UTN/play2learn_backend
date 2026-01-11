package trinity.play2learn.backend.profile.profile.services.interfaces;

import trinity.play2learn.backend.profile.profile.models.Profile;

public interface IProfileUpdateLevelService {
    
    public void execute(Profile profile, Long xp);

}