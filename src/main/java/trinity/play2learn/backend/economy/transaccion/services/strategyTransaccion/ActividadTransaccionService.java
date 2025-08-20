package trinity.play2learn.backend.economy.transaccion.services.strategyTransaccion;


import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRemoveBalanceService;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.economy.transaccion.mappers.TransaccionMapper;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.repositories.ITransaccionRepository;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.ITransaccionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service ("ACTIVIDAD")
@AllArgsConstructor
public class ActividadTransaccionService implements ITransaccionStrategyService {

    private final ITransaccionRepository transaccionRepository;

    private final ISubjectRemoveBalanceService subjectRemoveBalanceService;

    private final IReserveFindLastService reserveFindLastService;

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

        Transaccion transaccion = TransaccionMapper.toModel(
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
