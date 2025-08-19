package trinity.play2learn.backend.user.mapper;

import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;
import trinity.play2learn.backend.user.dtos.signUp.SignUpResponseDto;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

public class UserMapper {
    
    public static User toModel(String email, String password , Role role) {
        return User.builder()
            .email(email)
            .password(password)
            .role(role)
            .build();
    }

    public static SignUpResponseDto toSignUpDto(User user) {
        return SignUpResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

    public static LoginResponseDto toLoginDto(User user , String accessToken , String refreshToken , Object roleData) {
        return LoginResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .roleData(roleData)
            .build();
    }

    public static UserResponseDto toUserDto(User user) {
        return UserResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .build();
    }
}
