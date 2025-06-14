package trinity.play2learn.backend.user.services.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.user.interfaces.IUserService;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String UNAUTHORIZED_MESSAGE = "Email or password is incorrect";

    //Devuelve el usuario si existe y la contraseña es correcta. De lo contrario lanza una excepcion.
    @Override
    public User findUserOrThrowException(String email, String password) {

        User user = findUserOrThrowException(email);

        checkIfPasswordMatchesOrThrowException(password, user.getPassword());
       
        return user;
    }

    private User findUserOrThrowException(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException(UNAUTHORIZED_MESSAGE));
    }

    private void checkIfPasswordMatchesOrThrowException(String loginPassword, String dbPassword) {
        if (!passwordEncoder.matches(loginPassword, dbPassword)) {
            throw new UnauthorizedException(UNAUTHORIZED_MESSAGE);
        }
    }

}
