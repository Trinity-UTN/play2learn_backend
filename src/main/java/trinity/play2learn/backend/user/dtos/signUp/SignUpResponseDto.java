package trinity.play2learn.backend.user.dtos.signUp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.user.models.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {

    private Long id;
    private String token;
    private Role role;
    private String email;
}
