package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

public interface ISavingAccountFindByIdAndWalletService {
    
    public SavingAccount execute(Long id, Wallet wallet);

}
