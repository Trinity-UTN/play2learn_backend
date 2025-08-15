package trinity.play2learn.backend.economy.transaccion.services.strategyTransaccion;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRemoveBalanceService;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IModifyReserveService;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.repositories.ITransaccionRepository;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.IStrategyTransaccionService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IAddAmountWalletService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IRemoveAmountWalletService;

@Service ("RECOMPENSA")
@AllArgsConstructor
public class RecompensaTransaccionService implements IStrategyTransaccionService {

    private final ITransaccionRepository transaccionRepository;

    private final IRemoveAmountWalletService removeAmountWalletService;

    private final IAddAmountWalletService addAmountWalletService;

    private final IModifyReserveService modifyReserveService;

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

        Transaccion transaccion = Transaccion.builder()
        .amount(amount)
        .description(description)
        .origin(origin)
        .destination(destination)
        .wallet(wallet)
        .subject(subject)
        .build();

        Transaccion transaccionSaved = transaccionRepository.save(transaccion);

        Wallet walletUpdated = addAmountWalletService.execute(wallet, amount);

        Subject subejctUpdated = subjectRemoveBalanceService.execute(subject, amount);

        return transaccionSaved;
    }
    
}
