package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface ISavingAccountExistsByNameAndWalletService {

    public boolean execute (String name, Wallet wallet);
    
}
