package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityRemoveBalanceService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.transaction.mappers.TransactionMapper;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;
import trinity.play2learn.backend.investment.stock.models.Order;

@Service ("RECOMPENSA")
@AllArgsConstructor
public class RecompensaTransactionService implements ITransactionStrategyService {

    private final ITransactionRepository transaccionRepository;

    private final IWalletAddAmountService addAmountWalletService;

    private final IActivityRemoveBalanceService activityRemoveBalanceService;

    private final IReserveFindLastService findLastReserveService;

    @Override
    @Transactional
    public Transaction execute(
        Double amount, 
        String description, 
        TransactionActor origin, 
        TransactionActor destination,
        Wallet wallet, 
        Subject subject,
        Activity activity,
        Benefit benefit,
        Order order
        ) {
        
        if (activity.getActualBalance() < amount) {
            throw new ConflictException(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_SUBJECT);
        }

        Transaction transaccion = TransactionMapper.toModel(
            amount, 
            description, 
            origin, 
            destination, 
            wallet, 
            subject,
            activity,
            benefit,
            order,
            findLastReserveService.get()
        );

        Transaction transaccionSaved = transaccionRepository.save(transaccion);

        activityRemoveBalanceService.execute(activity, amount);

        addAmountWalletService.execute(wallet, amount);

        return transaccionSaved;
    }
    
}
