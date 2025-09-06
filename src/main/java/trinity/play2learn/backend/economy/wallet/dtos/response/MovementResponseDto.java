package trinity.play2learn.backend.economy.wallet.dtos.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MovementResponseDto {

    public Double amount;

    public LocalDateTime createdAt;

    public String description;

    public String type;
    
}
