package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface ISavingAccountCalculateAmountInvestedService {
    
    public Double execute (Wallet wallet);
    
}
