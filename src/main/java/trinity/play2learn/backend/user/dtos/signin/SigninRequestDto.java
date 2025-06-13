package trinity.play2learn.backend.user.dtos.signin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.user.models.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigninRequestDto {

    @Email
    private String email;

    @NotEmpty
    private String password;

    @NotNull
    private Role role;
    
}
