package trinity.play2learn.backend.user.dtos.changePassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";

    @NotBlank(message = "La contraseña actual no puede estar vacia.")
    private String oldPassword;

    @NotBlank(message = "La nueva contraseña no puede estar vacia.")
    @Pattern(regexp = PASSWORD_PATTERN, message = UnauthorizedExceptionMessages.PASSWORD_INVALID_FORMAT)
    private String newPassword;

}
