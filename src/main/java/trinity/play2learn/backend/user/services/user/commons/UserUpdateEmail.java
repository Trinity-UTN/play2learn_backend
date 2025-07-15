package trinity.play2learn.backend.user.services.user.commons;


import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.user.interfaces.IUserUpdateEmail;

@Service
@AllArgsConstructor
public class UserUpdateEmail implements IUserUpdateEmail {

    private final IUserRepository userRepository;

    @Override
    public void update (User user, String email) {
        user.setEmail(email);
        userRepository.save(user);
    }

    
}
