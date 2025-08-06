package trinity.play2learn.backend.profile.profile.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileGetByIdService;

@Service
@AllArgsConstructor
public class ProfileGetById implements IProfileGetByIdService {
    
    private final IProfileRepository profileRepository;

    @Override
    public Profile get(Long id) {
        return profileRepository.findById(id).orElseThrow(() ->
            new NotFoundException(
                NotFoundExceptionMesagges.resourceNotFound("Perfil", String.valueOf(id))
            )
        );
    }
    
}
