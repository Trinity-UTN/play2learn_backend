package trinity.play2learn.backend.economy.transaction.services.interfaces;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface ITransactionStrategyService {
    
    public Transaction execute (
        Double amount,
        String description,
        TransactionActor origin,
        TransactionActor destination,
        Wallet wallet,
        Subject subject,
        Activity activity,
        Benefit benefit
    );

}
