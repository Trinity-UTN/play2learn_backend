package trinity.play2learn.backend.economy.transaction.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionStrategyService;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service
@AllArgsConstructor
public class TransactionGenerateService implements ITransactionGenerateService{

    private final Map<String, ITransactionStrategyService> strategies;


    @Override
    @Transactional
    public Transaction generate(
        TypeTransaction type,
        Double amount, 
        String description, 
        TransactionActor origin, 
        TransactionActor destination, 
        Wallet wallet, 
        Subject subject,
        Activity activity
        ) {
        if (amount <= 0) {
            throw new ConflictException(EconomyMessages.AMOUNT_MAJOR_TO_0);
        }

        ITransactionStrategyService strategy = strategies.get(type.name());

        if (strategy == null) {
            throw new ConflictException(EconomyMessages.getTransactionNotSupported(type.name()));
        }

        Transaction transaccion = strategy.execute(
            amount, 
            description, 
            origin, 
            destination,
            wallet, 
            subject,
            activity
        );

        return transaccion;

    }
    
}
