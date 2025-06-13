package trinity.play2learn.backend.user.dtos.signin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.user.models.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigninResponseDto {

    private Long id;
    private String token;
    private Role role;
    private String email;
}
