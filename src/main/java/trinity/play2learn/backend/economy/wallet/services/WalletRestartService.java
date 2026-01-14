package trinity.play2learn.backend.economy.wallet.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveCreateNewService;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRestartService;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@Service
@AllArgsConstructor
public class WalletRestartService implements IWalletRestartService {
    

    private final IWalletRepository walletRepository;

    private final IReserveCreateNewService reserveCreateNewService;

    private final ITransactionGenerateService transactionGenerateService;

    @Override
    public void execute() {

        reserveCreateNewService.execute();
        
        List<Wallet> wallets = walletRepository.findAll();

        for (Wallet wallet : wallets) {
            transactionGenerateService.generate(
                TypeTransaction.REINICIO_WALLET,
                wallet.getBalance(),
                "Reinicio de wallet",
                TransactionActor.ESTUDIANTE,
                TransactionActor.SISTEMA,
                wallet,
                null,
                null,
                null,
                null,
                null, 
                null
            );
        }
    }
}
