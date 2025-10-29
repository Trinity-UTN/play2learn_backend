package trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;

@Data
@Builder
public class FixedTermDepositResponseDto {
    
    private Long id;

    private Double amountInvested;

    private Double amountReward;

    private FixedTermDays fixedTermDays;

    private LocalDate startDate;

    private LocalDate endDate;

    private FixedTermState fixedTermState; 

}
