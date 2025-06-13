package trinity.play2learn.backend.user.services.signin;


import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.dtos.signin.SigninRequestDto;
import trinity.play2learn.backend.user.dtos.signin.SigninResponseDto;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.signin.interfaces.ISigninService;

@Service
public class SigninService implements ISigninService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SigninService(IUserRepository userRepository , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SigninResponseDto signin(SigninRequestDto signinDto , BindingResult result) {
    
        if (result.hasFieldErrors()) {
            throw new BadRequestException("An error ocurred: ", result.getFieldErrors().stream().map(err -> err.getField() + ": " + err.getDefaultMessage()).toList());
        }

        Optional<User> optionalUser = userRepository.findByEmail(signinDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new ConflictException("User already exists");
        }

        String encryptPassword = passwordEncoder.encode(signinDto.getPassword());
        User userToSave = UserMapper.toModel(signinDto, encryptPassword);

        return UserMapper.toSignInDto(userRepository.save(userToSave));
    }
    
}
