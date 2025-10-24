package trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;

@Data
@Builder
public class FixedTermDepositResponseDto {
    
    private Long id;

    private Double amountInvested;

    private Double amountReward;

    private FixedTermDays fixedTermDays;

    private LocalDate startDate;

    private LocalDate endDate;

}
