package trinity.play2learn.backend.user.services.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.user.interfaces.IUserGetByEmailService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserService;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final PasswordEncoder passwordEncoder;
    private final IUserGetByEmailService userFindService;

    private final String UNAUTHORIZED_MESSAGE = "Invalid credentials or unauthorized access.";

    //Devuelve el usuario si existe y la contraseña es correcta. De lo contrario lanza una excepcion.
    @Override
    public User findUserByEmailAndPassword(String email, String password) {

        User user = userFindService.findUserByEmail(email); //Devuelve UnauthorizedException si no encuentra al usuario

        checkIfPasswordMatches(password, user.getPassword());
       
        return user;
    }

    private void checkIfPasswordMatches(String loginPassword, String dbPassword) {
        if (!passwordEncoder.matches(loginPassword, dbPassword)) {
            throw new UnauthorizedException(UNAUTHORIZED_MESSAGE);
        }
    }

}
