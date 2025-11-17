package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountRegisterRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface ISavingAccountRegisterService {

    public SavingAccountResponseDto cu102registerSavingAccount (
        SavingAccountRegisterRequestDto dto,
        User user
    );
    
}
