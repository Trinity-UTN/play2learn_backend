package trinity.play2learn.backend.user.dtos.token;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenDto {
    
    @NotBlank (message = ValidationMessages.NOT_EMPTY_TOKEN)
    private String refreshToken;
}
