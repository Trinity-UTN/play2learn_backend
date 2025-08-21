package trinity.play2learn.backend.economy.transaccion.services.strategyTransaccion;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
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
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;

@Service ("COMPRA")
@AllArgsConstructor
public class CompraTransaccionService implements ITransaccionStrategyService{

    private final ITransaccionRepository transaccionRepository;

    private final IWalletRemoveAmountService removeAmountWalletService;

    private final IReserveModifyService modifyReserveService;

    private final IReserveFindLastService findLastReserveService;

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
        
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_STUDENT);
        }

        Reserve reserve = findLastReserveService.get();

        Transaccion transaccion = TransaccionMapper.toModel(
            amount, 
            description, 
            origin, 
            destination, 
            wallet, 
            null,
            reserve
        );

        Transaccion transaccionSaved = transaccionRepository.save(transaccion);

        Wallet walletUpdated = removeAmountWalletService.execute(wallet, amount);

        modifyReserveService.moveToReserve(amount, reserve);

        return transaccionSaved;
    }
    
}
