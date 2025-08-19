package trinity.play2learn.backend.economy.transaccion.services.strategyTransaccion;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddBalanceService;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaccion.mappers.TransaccionMapper;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.repositories.ITransaccionRepository;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.ITransaccionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service ("ASIGNACION")
@AllArgsConstructor
public class AsignacionTransaccionService implements ITransaccionStrategyService{

    private final IReserveFindLastService findLastReserveService;

    private final ISubjectAddBalanceService subjectAddBalanceService;

    private final ITransaccionRepository transaccionRepository;

    private final IReserveModifyService modifyReserveService;
    
    @Override
    @Transactional
    public Transaccion execute(
        Double amount, 
        String description, 
        ActorTransaccion origin, 
        ActorTransaccion destination,
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

        Transaccion transaccion = TransaccionMapper.toModel(
            assignAmount, 
            description, 
            origin, 
            destination, 
            null, 
            subject,
            reserve
        );

        Transaccion transaccionSaved = transaccionRepository.save(transaccion);

        Subject subejctUpdated = subjectAddBalanceService.execute(subject, amount);

        modifyReserveService.moveToCirculation(amount);

        return transaccionSaved;
    }
    
}
