package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountWithdrawalRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface ISavingAccountWithdrawalService {

    public SavingAccountResponseDto cu104withdrawalSavingAccount (SavingAccountWithdrawalRequestDto dto, User user);
    
} 
