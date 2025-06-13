package trinity.play2learn.backend.user.services.user;

import java.util.Optional;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.user.interfaces.IUserService;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //Devuelve el usuario si existe y la contraseña es correcta. De lo contrario lanza una excepcion.
    @Override
    public User findUserOrThrowException(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        validateEmail(optionalUser);
        validatePassword(optionalUser , password);

        return optionalUser.get();
    }

    //Valida que exista un usuario con ese email en la base de datos
    private void validateEmail(Optional<User> optionalUser) {
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
    }

    //Valida que la contraseña recibida coincida con la de la base de datos
    private void validatePassword(Optional<User> optionalUser , String password) {

        if (passwordEncoder.matches(optionalUser.get().getPassword() , password)) {
            throw new UnauthorizedException("Password is incorrect");
        }
    }
}
