package trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedTermDepositRegisterRequestDto {

    private Double amountInvested;

    private FixedTermDays fixedTermDays;
    
}
