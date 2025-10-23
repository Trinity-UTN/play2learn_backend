package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.math.BigInteger;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IStockCalculateByWalletService {
    
    public BigInteger execute (Stock stock, Wallet wallet);
    
}
