package trinity.play2learn.backend.economy.wallet.services.interfaces;

import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface IAddAmountWalletService {
    
    public Wallet execute (Wallet wallet, Double amount);
    
}
