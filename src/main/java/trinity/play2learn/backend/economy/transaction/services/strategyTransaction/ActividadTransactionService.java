package trinity.play2learn.backend.economy.transaction.services.strategyTransaction;


import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRemoveBalanceService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.transaction.mappers.TransactionMapper;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service ("ACTIVIDAD")
@AllArgsConstructor
public class ActividadTransactionService implements ITransactionStrategyService {

    private final ITransactionRepository transaccionRepository;

    private final ISubjectRemoveBalanceService subjectRemoveBalanceService;

    private final IReserveFindLastService reserveFindLastService;

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

        if (amount > (0.3 * subject.getInitialBalance())){
            throw new IllegalArgumentException(
                "El monto de la actividad no puede ser mayor al 30% del balance inicial de la materia"
            );
        }

        if (amount > subject.getActualBalance()){
            throw new IllegalArgumentException(
                "La materia no cuenta con el balance suficiente para realizar la actividad"
            );
        }

        Transaction transaccion = TransactionMapper.toModel(
            amount, 
            description, 
            origin, 
            destination, 
            wallet, 
            subject,
            reserveFindLastService.get()
        );

        subjectRemoveBalanceService.execute(subject, amount);

        return transaccionRepository.save(transaccion);
    }
    

}
