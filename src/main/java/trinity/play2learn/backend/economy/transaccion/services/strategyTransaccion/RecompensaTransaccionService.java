package trinity.play2learn.backend.economy.transaccion.services.strategyTransaccion;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRemoveBalanceService;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveModifyService;
import trinity.play2learn.backend.economy.transaccion.mappers.TransaccionMapper;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.repositories.ITransaccionRepository;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.ITransaccionStrategyService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;

@Service ("RECOMPENSA")
@AllArgsConstructor
public class RecompensaTransaccionService implements ITransaccionStrategyService {

    private final ITransaccionRepository transaccionRepository;

    private final IWalletRemoveAmountService removeAmountWalletService;

    private final IWalletAddAmountService addAmountWalletService;

    private final IReserveModifyService modifyReserveService;

    private final ISubjectRemoveBalanceService subjectRemoveBalanceService;

    @Override
    public Transaccion execute(
        Double amount, 
        String description, 
        ActorTransaccion origin, 
        ActorTransaccion destination,
        Wallet wallet, 
        Subject subject) {
        
        if (subject.getActualBalance() < amount) {
            throw new IllegalArgumentException(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_SUBJECT);
        }

        Transaccion transaccion = TransaccionMapper.toModel(
            amount, 
            description, 
            origin, 
            destination, 
            wallet, 
            subject
        );

        Transaccion transaccionSaved = transaccionRepository.save(transaccion);

        Wallet walletUpdated = addAmountWalletService.execute(wallet, amount);

        Subject subejctUpdated = subjectRemoveBalanceService.execute(subject, amount);

        return transaccionSaved;
    }
    
}
