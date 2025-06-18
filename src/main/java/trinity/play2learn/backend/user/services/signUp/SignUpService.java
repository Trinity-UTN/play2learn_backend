package trinity.play2learn.backend.user.services.signUp;


import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.dtos.signUp.SignUpRequestDto;
import trinity.play2learn.backend.user.dtos.signUp.SignUpResponseDto;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.repository.IUserRepository;
import trinity.play2learn.backend.user.services.signUp.interfaces.ISignUpService;

@Service
public class SignUpService implements ISignUpService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpService(IUserRepository userRepository , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpDto) {

        Optional<User> optionalUser = userRepository.findByEmail(signUpDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new ConflictException("User already exists");
        }

        String encryptPassword = passwordEncoder.encode(signUpDto.getPassword());
        User userToSave = UserMapper.toModel(signUpDto, encryptPassword);

        return UserMapper.toSignUpDto(userRepository.save(userToSave));
    }
    
}
