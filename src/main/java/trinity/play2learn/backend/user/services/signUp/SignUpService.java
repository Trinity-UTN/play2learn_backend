package trinity.play2learn.backend.user.services.signUp;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.dtos.signUp.SignUpRequestDto;
import trinity.play2learn.backend.user.dtos.signUp.SignUpResponseDto;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.signUp.interfaces.ISignUpService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserCreateService;

@Service
@AllArgsConstructor
public class SignUpService implements ISignUpService {

    private final IUserCreateService userCreateService;

    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpDto , String role) {
        
        Role roleToSave = Role.valueOf(role);
        
        //Lanza un 409 si el email ya existe en un usuario activo en la base de datos
        User userSaved = userCreateService.create(signUpDto.getEmail(), signUpDto.getPassword(), roleToSave);

        return UserMapper.toSignUpDto(userSaved);
    }
    
}
