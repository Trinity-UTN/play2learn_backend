package trinity.play2learn.backend.economy.wallet.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WalletResponseDto {

    private Long id;
    private Double balance;
    private Double invertedBalance;
    
}
