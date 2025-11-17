package trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces;

import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface IFixedTermDepositCalculateAmountInvestedService {
    
    public Double execute (Wallet wallet);
    
}
