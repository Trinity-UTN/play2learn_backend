package trinity.play2learn.backend.user.dtos.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    @Email(message = ValidationMessages.PATTERN_EMAIL)
    @NotBlank(message = ValidationMessages.NOT_EMPTY_EMAIL)
    private String email;

    @NotBlank(message = ValidationMessages.NOT_EMPTY_PASSWORD)
    private String password;
}
