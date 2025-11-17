package trinity.play2learn.backend.profile.profile.services.interfaces;


import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.profile.models.Profile;

public interface IProfileContainsAspectService {

    public boolean execute(Profile profile, Aspect aspect);

}
