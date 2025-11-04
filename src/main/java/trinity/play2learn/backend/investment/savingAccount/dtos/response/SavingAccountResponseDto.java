package trinity.play2learn.backend.investment.savingAccount.dtos.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavingAccountResponseDto {

    private Long id;

    private Double initialAmount;

    private Double currentAmount;

    private LocalDate startDate;

    private LocalDate lastUpdate;
    
}
