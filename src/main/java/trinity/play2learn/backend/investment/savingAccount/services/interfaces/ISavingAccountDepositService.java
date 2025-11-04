package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountDepositRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface ISavingAccountDepositService {
    
    public SavingAccountResponseDto cu103depositSavingAccount (SavingAccountDepositRequestDto dto, User user);

}
