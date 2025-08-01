package trinity.play2learn.backend.user.services.user.commons;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;
import trinity.play2learn.backend.user.dtos.signUp.SignUpRequestDto;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.user.interfaces.IUserCreateService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserExistService;

@Service
@AllArgsConstructor
public class UserCreateService implements IUserCreateService {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    private final IUserExistService userExistService;

    @Override
    public User create(String email, String password, Role role) {

        if (userExistService.validate(email)) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExistsByAtribute(
                    "Usuario", 
                    "email",
                    email
                )
            );
        }

        String encryptPassword = passwordEncoder.encode(password);

        SignUpRequestDto signUpDto = new SignUpRequestDto();
        signUpDto.setEmail(email);
        signUpDto.setPassword(encryptPassword);
        signUpDto.setRole(role);

        User userToSave = UserMapper.toModel(signUpDto, encryptPassword);

        return userRepository.save(userToSave);
    }
    
}
