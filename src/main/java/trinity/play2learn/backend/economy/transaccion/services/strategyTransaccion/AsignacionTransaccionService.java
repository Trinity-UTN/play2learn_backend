package trinity.play2learn.backend.economy.transaccion.services.strategyTransaccion;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddBalanceService;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IFindLastReserveService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IModifyReserveService;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.repositories.ITransaccionRepository;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.IStrategyTransaccionService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service ("ASIGNACION")
@AllArgsConstructor
public class AsignacionTransaccionService implements IStrategyTransaccionService{

    private final IFindLastReserveService findLastReserveService;

    private final ISubjectAddBalanceService subjectAddBalanceService;

    private final ITransaccionRepository transaccionRepository;

    private final IModifyReserveService modifyReserveService;
    
    @Override
    public Transaccion execute(
        Double amount, 
        String description, 
        ActorTransaccion origin, 
        ActorTransaccion destination,
        Wallet wallet, 
        Subject subject) {

        Double assignAmount = subject.getInitialBalance() - subject.getActualBalance();

        if (!assignAmount.equals(amount)) {
            throw new UnsupportedOperationException(EconomyMessages.INCORRECT_AMOUNT);
        }

        Reserve reserve = findLastReserveService.get();

        if (reserve.getReserveBalance() < amount) {
            reserve.setReserveBalance(reserve.getReserveBalance() + (amount - reserve.getReserveBalance()));
        }

        Transaccion transaccion = Transaccion.builder()
        .amount(amount)
        .description(description)
        .origin(origin)
        .destination(destination)
        .wallet(null)
        .subject(subject)
        .build();

        Transaccion transaccionSaved = transaccionRepository.save(transaccion);

        Subject subejctUpdated = subjectAddBalanceService.execute(subject, amount);

        modifyReserveService.moveToCirculation(amount);

        return transaccionSaved;
    }
    
}
