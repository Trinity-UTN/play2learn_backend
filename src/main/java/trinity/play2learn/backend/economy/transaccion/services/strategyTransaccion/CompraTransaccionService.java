package trinity.play2learn.backend.economy.transaccion.services.strategyTransaccion;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IModifyReserveService;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.repositories.ITransaccionRepository;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.IStrategyTransaccionService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IRemoveAmountWalletService;

@Service ("COMPRA")
@AllArgsConstructor
public class CompraTransaccionService implements IStrategyTransaccionService{

    private final ITransaccionRepository transaccionRepository;

    private final IRemoveAmountWalletService removeAmountWalletService;

    private final IModifyReserveService modifyReserveService;

    @Override
    public Transaccion execute(
        Double amount, 
        String description, 
        ActorTransaccion origin, 
        ActorTransaccion destination,
        Wallet wallet, 
        Subject subject) {
        
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_STUDENT);
        }

        Transaccion transaccion = Transaccion.builder()
        .amount(amount)
        .description(description)
        .origin(origin)
        .destination(destination)
        .wallet(wallet)
        .subject(null)
        .build();

        Transaccion transaccionSaved = transaccionRepository.save(transaccion);

        Wallet walletUpdated = removeAmountWalletService.execute(wallet, amount);

        modifyReserveService.moveToReserve(amount);

        return transaccionSaved;
    }
    
}
