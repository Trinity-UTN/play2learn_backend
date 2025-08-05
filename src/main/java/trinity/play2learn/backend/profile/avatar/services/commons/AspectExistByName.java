package trinity.play2learn.backend.profile.avatar.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectExistByName;

@Service
@AllArgsConstructor
public class AspectExistByName implements IAspectExistByName {

    private final IAspectRepository aspectRepository;
    
    @Override
    public boolean validate (String name) {
        return aspectRepository.existsByName(name);
    }
    
}
