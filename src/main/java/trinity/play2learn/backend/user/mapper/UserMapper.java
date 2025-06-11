package trinity.play2learn.backend.user.mapper;

import trinity.play2learn.backend.user.dtos.SigninRequestDto;
import trinity.play2learn.backend.user.dtos.SigninResponseDto;
import trinity.play2learn.backend.user.models.User;

public class UserMapper {
    
    public static User toModel(SigninRequestDto signinDto, String password) {
        return User.builder()
            .email(signinDto.getEmail())
            .password(password)
            .role(signinDto.getRole())
            .build();
    }

    public static SigninResponseDto toDto(User user) {
        return SigninResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

}
