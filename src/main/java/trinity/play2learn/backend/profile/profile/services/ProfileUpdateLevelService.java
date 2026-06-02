package trinity.play2learn.backend.profile.profile.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.configs.levels.ValueXp;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileUpdateLevelService;

@Service
@AllArgsConstructor
public class ProfileUpdateLevelService implements IProfileUpdateLevelService {

    private final IProfileRepository profileRepository;
    
    @Override
    public void execute(Profile profile, Long xp) {

        Long currentXp =(profile.getCurrentXp() != null) ? profile.getCurrentXp() : 0L;
        Long currentLevel =(profile.getCurrentLevel() != null) ? profile.getCurrentLevel() : 1L;

        Long newXp = currentXp + xp;

        if (newXp >= (currentLevel*currentLevel) * ValueXp.BASE.getValue()) {
            newXp = newXp - ((currentLevel*currentLevel) * ValueXp.BASE.getValue());
            currentLevel = currentLevel + 1;
        }
        
        profile.setCurrentXp(newXp);
        profile.setCurrentLevel(currentLevel);

        profileRepository.save(profile);
    }
}
