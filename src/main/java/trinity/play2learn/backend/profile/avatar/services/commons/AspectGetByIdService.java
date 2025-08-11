package trinity.play2learn.backend.profile.avatar.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectGetByIdService;

@Service
@AllArgsConstructor
public class AspectGetByIdService implements IAspectGetByIdService{

    private final IAspectRepository aspectRepository;

    @Override
    public Aspect get(Long id) {
        return aspectRepository.findById(id).orElseThrow(() -> new NotFoundException(
                NotFoundExceptionMesagges.resourceNotFoundById("Aspecto", String.valueOf(id))
            )
        );
    }
    
}
