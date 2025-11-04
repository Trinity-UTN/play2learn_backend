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
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderType;

@Service ("STOCK")
@AllArgsConstructor
public class StockTransactionService implements ITransactionStrategyService {
    
    private final ITransactionRepository transaccionRepository;

    private final IWalletRemoveAmountService removeAmountWalletService;

    private final IReserveModifyService modifyReserveService;

    private final IReserveFindLastService findLastReserveService;

    private final IWalletAddAmountService addAmountWalletService;

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
        /*
         * Cosas a hacer
         * 1. Verificar que la orden sea de compra o venta
         * 2. Si es de compra, verificar que el wallet tenga saldo suficiente
         * 3. Si es de venta, verificar que el wallet tenga las acciones suficientes
         * 4. Realizar la transacción correspondiente
         * 5. Actualizar el wallet
         */
        Reserve reserve = findLastReserveService.get();

        Transaction transaccionSaved = null;

        if (order.getOrderType() == OrderType.COMPRA){
            
            if (amount > wallet.getBalance()){
                throw new BadRequestException(
                    "El wallet no cuenta con el balance suficiente para realizar la compra de acciones"
                );
            }

            Transaction transaccion = TransactionMapper.toModel(
                amount, 
                description, 
                origin, 
                destination, 
                wallet, 
                null,
                null,
                null,
                order,
                fixedTermDeposit,
                null,
                reserve
            );

            transaccionSaved = transaccionRepository.save(transaccion);

            removeAmountWalletService.execute(wallet, amount);

            modifyReserveService.moveToReserve(amount, reserve);

        }else {

            Transaction transaccion = TransactionMapper.toModel(
                amount, 
                description, 
                origin, 
                destination, 
                wallet, 
                null,
                null,
                null,
                order,
                fixedTermDeposit,
                null,
                reserve
            );

            transaccionSaved = transaccionRepository.save(transaccion);

            addAmountWalletService.execute(wallet, amount);

            modifyReserveService.moveToCirculation(amount, reserve);

        }

        return transaccionSaved;
    }
    
}
