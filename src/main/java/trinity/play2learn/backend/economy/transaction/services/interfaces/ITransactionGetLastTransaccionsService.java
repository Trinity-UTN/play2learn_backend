package trinity.play2learn.backend.economy.transaction.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface ITransactionGetLastTransaccionsService {
    
    public List<Transaction> execute (Wallet wallet);
    
}
