package trinity.play2learn.backend.economy.wallet.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionResponseDto;

@Data
@Builder
@AllArgsConstructor
public class WalletCompleteResponseDto {
    
    private Long id;

    private Double balance;

    private Double invertedBalance;

    private List<TransactionResponseDto> transactions;

}
