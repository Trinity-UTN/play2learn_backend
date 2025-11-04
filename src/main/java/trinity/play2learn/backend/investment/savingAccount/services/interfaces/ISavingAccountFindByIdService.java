package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

public interface ISavingAccountFindByIdService {
    
    public SavingAccount execute(Long id);

}
