package trinity.play2learn.backend.user.dtos.changePassword;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    
    @NotBlank(message = "La contraseña actual no puede estar vacia.")
    private String oldPassword;

    @NotBlank(message = "La nueva contraseña no puede estar vacia.")
    private String newPassword;

}
