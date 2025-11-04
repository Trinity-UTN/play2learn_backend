package trinity.play2learn.backend.investment.savingAccount.mappers;

import java.time.LocalDate;
import java.util.List;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountRegisterRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

public class SavingAccountMapper {

    public static SavingAccount toModel(SavingAccountRegisterRequestDto savingAccountDto, Wallet wallet) {
        return SavingAccount.builder()
            .name(savingAccountDto.getName())
            .startDate(LocalDate.now())
            .initialAmount(savingAccountDto.getInitialAmount())
            .currentAmount(savingAccountDto.getInitialAmount())
            .lastUpdate(LocalDate.now())
            .wallet(wallet)
            .build();
    }

    
    public static SavingAccountResponseDto toDto (SavingAccount savingAccount) {
        return SavingAccountResponseDto.builder()
            .id(savingAccount.getId())
            .initialAmount(savingAccount.getInitialAmount())
            .currentAmount(savingAccount.getCurrentAmount())
            .startDate(savingAccount.getStartDate())
            .lastUpdate(savingAccount.getLastUpdate())
            .build();
    }

    public static List<SavingAccountResponseDto> toDtoList (List<SavingAccount> savingAccounts) {
        return savingAccounts.stream().map(SavingAccountMapper::toDto).toList();
    }
    
}
