package trinity.play2learn.backend.user.services.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.user.interfaces.IUserChangePasswordService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserValidateEqualsPasswordService;
import trinity.play2learn.backend.user.repository.IUserRepository;

@Service
@AllArgsConstructor
public class UserChangePasswordService implements IUserChangePasswordService {
    
    private final IUserValidateEqualsPasswordService validateEqualsPasswordService;

    private final PasswordEncoder passwordEncoder;

    private final IUserRepository userRepository;

    @Override
    @Transactional
    public void cu113ChangePassword(User user, String oldPassword, String newPassword) {

        if (!validateEqualsPasswordService.validate(oldPassword, user.getPassword())) { //Valida que la contraseña actual sea correcta
            throw new UnauthorizedException(UnauthorizedExceptionMessages.PASSWORD_INVALID_FORMAT);
        }

        if (validateEqualsPasswordService.validate(newPassword, user.getPassword())) { //Valida que la nueva contraseña sea diferente a la actual
            throw new UnauthorizedException(UnauthorizedExceptionMessages.PASSWORD_INVALID_FORMAT);
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}
