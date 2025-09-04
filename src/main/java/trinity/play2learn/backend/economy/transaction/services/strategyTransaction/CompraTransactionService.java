package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaction.mappers.TransactionMapper;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;

@Service ("COMPRA")
@AllArgsConstructor
public class CompraTransactionService implements ITransactionStrategyService{

    private final ITransactionRepository transaccionRepository;

    private final IWalletRemoveAmountService removeAmountWalletService;

    private final IReserveModifyService modifyReserveService;

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
        Activity activity
        ) {
        
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_STUDENT);
        }

        Reserve reserve = findLastReserveService.get();

        Transaction transaccion = TransactionMapper.toModel(
            amount, 
            description, 
            origin, 
            destination, 
            wallet, 
            null,
            null,
            reserve
        );

        Transaction transaccionSaved = transaccionRepository.save(transaccion);

        Wallet walletUpdated = removeAmountWalletService.execute(wallet, amount);

        modifyReserveService.moveToReserve(amount, reserve);

        return transaccionSaved;
    }
    
}
