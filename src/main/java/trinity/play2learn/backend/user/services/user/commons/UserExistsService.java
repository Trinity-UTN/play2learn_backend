package trinity.play2learn.backend.user.services.user.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.user.interfaces.IUserExistService;
@Service
@AllArgsConstructor
public class UserExistsService implements IUserExistService {

    private final IUserRepository userRepository;

    @Override
    public boolean validate(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }
    
}
