package trinity.play2learn.backend.profile.profile.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileContainsAspectService;

@Service
@AllArgsConstructor
public class ProfileContainsAspectService implements IProfileContainsAspectService {
    
    @Override
    public boolean execute(Profile profile, Aspect aspect) {
        return profile.getOwnedAspects().contains(aspect);
    }
    
}
