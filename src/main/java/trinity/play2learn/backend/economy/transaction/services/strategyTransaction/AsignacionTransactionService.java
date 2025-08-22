package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddBalanceService;
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

@Service ("ASIGNACION")
@AllArgsConstructor
public class AsignacionTransactionService implements ITransactionStrategyService{

    private final IReserveFindLastService findLastReserveService;

    private final ISubjectAddBalanceService subjectAddBalanceService;

    private final ITransactionRepository transaccionRepository;

    private final IReserveModifyService modifyReserveService;
    
    @Override
    @Transactional
    public Transaction execute(
        Double amount, 
        String description, 
        TransactionActor origin, 
        TransactionActor destination,
        Wallet wallet, 
        Subject subject
        ) {

        Double assignAmount = subject.getInitialBalance() - subject.getActualBalance();

        if (!assignAmount.equals(amount)) {
            throw new UnsupportedOperationException(EconomyMessages.INCORRECT_AMOUNT);
        }

        Reserve reserve = findLastReserveService.get();

        if (reserve.getReserveBalance() < amount) {
            reserve.setReserveBalance(reserve.getReserveBalance() + (amount - reserve.getReserveBalance()));
        }



        Transaction transaccion = TransactionMapper.toModel(
            assignAmount, 
            description, 
            origin, 
            destination, 
            null, 
            subject,
            reserve
        );

        Transaction transaccionSaved = transaccionRepository.save(transaccion);

        Subject subejctUpdated = subjectAddBalanceService.execute(subject, amount);

        Reserve reserveUpdated = modifyReserveService.moveToCirculation(amount, reserve);

        return transaccionSaved;
    }
    
}
