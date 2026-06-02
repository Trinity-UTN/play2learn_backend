package trinity.play2learn.backend.user.dtos.signUp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";

    @Email(message = ValidationMessages.PATTERN_EMAIL)
    @NotBlank(message = ValidationMessages.NOT_EMPTY_EMAIL)
    private String email;

    @NotBlank(message = ValidationMessages.NOT_EMPTY_PASSWORD)
    @Pattern(regexp = PASSWORD_PATTERN, message = UnauthorizedExceptionMessages.PASSWORD_INVALID_FORMAT)
    private String password;
    
}
