package trinity.play2learn.backend.investment.fixedTermDeposit.mappers;

import java.time.LocalDate;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;

public class FixedTermDepositMapper {

    public static FixedTermDeposit toEntity (
        FixedTermDepositRegisterRequestDto dto,
        Double amountReward,
        LocalDate startDate,
        LocalDate endDate,
        Wallet wallet,
        FixedTermState fixedTermState
    ){
        return FixedTermDeposit.builder()
            .amountInvested(dto.getAmountInvested())
            .amountReward(amountReward)
            .fixedTermDays(dto.getFixedTermDays())            
            .startDate(startDate)
            .endDate(endDate)
            .fixedTermState(fixedTermState)
            .wallet(wallet)
            .build();
    }

    public static FixedTermDepositResponseDto toDto (FixedTermDeposit fixedTermDeposit){
        return FixedTermDepositResponseDto.builder()
            .id(fixedTermDeposit.getId())
            .amountInvested(fixedTermDeposit.getAmountInvested())
            .amountReward(fixedTermDeposit.getAmountReward())
            .fixedTermDays(fixedTermDeposit.getFixedTermDays())
            .startDate(fixedTermDeposit.getStartDate())
            .endDate(fixedTermDeposit.getEndDate())
            .build();
    };
    
}
