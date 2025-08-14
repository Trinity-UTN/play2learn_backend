package trinity.play2learn.backend.economy.wallet.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IRemoveAmountWalletService;

@Service
@AllArgsConstructor
public class RemoveAmountWalletService implements IRemoveAmountWalletService {
    
    private final IWalletRepository walletRepository;
    
    @Override
    public Wallet execute(Wallet wallet, Double amount) {
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException("El estudiante no tiene suficiente dinero para realizar la transaccion");
        } else {
            wallet.setBalance(wallet.getBalance() - amount);
            return walletRepository.save(wallet);
        }
    }
    
}
