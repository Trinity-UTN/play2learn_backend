package trinity.play2learn.backend.user.mapper;

import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;
import trinity.play2learn.backend.user.dtos.signUp.SignUpRequestDto;
import trinity.play2learn.backend.user.dtos.signUp.SignUpResponseDto;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;
import trinity.play2learn.backend.user.models.User;

public class UserMapper {
    
    public static User toModel(SignUpRequestDto signUpDto, String password) {
        return User.builder()
            .email(signUpDto.getEmail())
            .password(password)
            .role(signUpDto.getRole())
            .build();
    }

    public static SignUpResponseDto toSignUpDto(User user) {
        return SignUpResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

    public static LoginResponseDto toLoginDto(User user , String token) {
        return LoginResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .token(token)
            .build();
    }

    public static UserResponseDto toUserDto(User user) {
        return UserResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .build();
    }
}
