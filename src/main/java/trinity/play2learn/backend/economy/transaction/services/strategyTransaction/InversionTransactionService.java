package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service ("INVERSION")
@AllArgsConstructor
public class InversionTransactionService implements ITransactionStrategyService{
    
    @Override
    @Transactional
    public Transaction execute(
        Double amount, 
        String description, 
        TransactionActor origin, 
        TransactionActor destination,
        Wallet wallet, 
        Subject subject,
        Activity activity
        ) {
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}
