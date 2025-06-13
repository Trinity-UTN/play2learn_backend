package trinity.play2learn.backend.user.dtos.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.user.models.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    //El frontend unicamente necesita de token. El email y role sirven para testear desde el postman. 
    private String email;
    private Role role;
    private String token;
}
