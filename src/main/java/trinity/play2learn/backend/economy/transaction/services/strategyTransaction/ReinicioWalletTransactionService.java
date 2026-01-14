package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaction.mappers.TransactionMapper;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionStrategyService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.stock.models.Order;
import jakarta.transaction.Transactional;

@Service ("REINICIO_WALLET")
@AllArgsConstructor
public class ReinicioWalletTransactionService implements ITransactionStrategyService {

    private final IReserveFindLastService findLastReserveService;

    private final ITransactionRepository transaccionRepository;

    private final IWalletRemoveAmountService removeAmountWalletService;

    private final IReserveModifyService modifyReserveService;

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
        Order order,
        FixedTermDeposit fixedTermDeposit,
        SavingAccount savingAccount
    ) {

        Reserve reserve = findLastReserveService.get();

        Transaction transaccion = TransactionMapper.toModel(
            amount, 
            description, 
            origin, 
            destination, 
            wallet, 
            null,
            null,
            null,
            null,
            null,
            null,
            reserve
        );

        Transaction transaccionSaved = transaccionRepository.save(transaccion);

        removeAmountWalletService.execute(wallet, amount);

        modifyReserveService.moveToReserve(amount, reserve);

        return transaccionSaved;

    }
}
