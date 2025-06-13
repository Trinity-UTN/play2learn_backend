package trinity.play2learn.backend.user.mapper;

import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;
import trinity.play2learn.backend.user.dtos.signin.SigninRequestDto;
import trinity.play2learn.backend.user.dtos.signin.SigninResponseDto;
import trinity.play2learn.backend.user.models.User;

public class UserMapper {
    
    public static User toModel(SigninRequestDto signinDto, String password) {
        return User.builder()
            .email(signinDto.getEmail())
            .password(password)
            .role(signinDto.getRole())
            .build();
    }

    public static SigninResponseDto toSignInDto(User user) {
        return SigninResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

    public static LoginResponseDto toLoginDto(User user , String token) {
        return LoginResponseDto.builder()
            .email(user.getEmail())
            .role(user.getRole())
            .token(token)
            .build();
    }
}
