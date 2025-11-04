package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface IStockCalculateAmountInvestedService {
    
    public Double execute (Wallet wallet);
    
}
