package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
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
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.stock.models.Order;

@Service ("INGRESO_CAJA_AHORRO")
@AllArgsConstructor
public class DepositSavingAccountTransactionService implements ITransactionStrategyService{
    
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
        Activity activity, 
        Benefit benefit, 
        Order order,
        FixedTermDeposit fixedTermDeposit, 
        SavingAccount savingAccount
    ) {

        if (amount <= 0) {
            throw new BadRequestException("El monto debe ser mayor a 0");
        }

        if (amount > wallet.getBalance()){
            throw new BadRequestException(
                "El wallet no cuenta con el balance suficiente para realizar el deposito en la caja de ahorro"
            );
        }

        Reserve reserve = findLastReserveService.get();

        Transaction transaccion = transaccionRepository.save(
            TransactionMapper.toModel(
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
                savingAccount,
                reserve
            )
        );

        removeAmountWalletService.execute(wallet, amount);

        modifyReserveService.moveToReserve(amount, reserve);
        
        return transaccion;
        
    }
    
}
