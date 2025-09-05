package trinity.play2learn.backend.economy.transaction.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGetLastTransaccionsService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service
@AllArgsConstructor
public class TransactionGetLastTransactionsService implements ITransactionGetLastTransaccionsService {
    
    private final ITransactionRepository transactionRepository;
    
    @Override
    public List<Transaction> execute(Wallet wallet) {
        return transactionRepository.findTop10ByWalletOrderByCreatedAtDesc(wallet);
    }
    
}
