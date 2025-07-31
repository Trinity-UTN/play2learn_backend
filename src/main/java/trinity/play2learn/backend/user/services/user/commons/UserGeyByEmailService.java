package trinity.play2learn.backend.user.services.user.commons;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.user.interfaces.IUserGetByEmailService;

@Service
@AllArgsConstructor
public class UserGeyByEmailService implements IUserGetByEmailService {
    
    private final IUserRepository userRepository;
    private final String UNAUTHORIZED_MESSAGE = "Invalid credentials or unauthorized access.";

    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UnauthorizedException(UNAUTHORIZED_MESSAGE));
        
    }
    
}
