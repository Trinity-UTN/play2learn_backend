package trinity.play2learn.backend.user.dtos.token;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenDto {
    
    @NotBlank
    private String refreshToken;
}
