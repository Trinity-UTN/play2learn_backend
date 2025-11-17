package trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedTermDepositRegisterRequestDto {
    @Positive
    @NotNull
    private Double amountInvested;

    @NotNull
    private FixedTermDays fixedTermDays;
    
}
