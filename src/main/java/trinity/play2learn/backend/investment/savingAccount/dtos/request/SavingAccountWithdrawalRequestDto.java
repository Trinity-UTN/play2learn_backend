package trinity.play2learn.backend.investment.savingAccount.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountWithdrawalRequestDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    @Positive
    private Double amount;
    
}
