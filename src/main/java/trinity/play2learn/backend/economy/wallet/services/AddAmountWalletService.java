package trinity.play2learn.backend.economy.wallet.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IAddAmountWalletService;

@Service
@AllArgsConstructor
public class AddAmountWalletService implements IAddAmountWalletService {

    private final IWalletRepository walletRepository;

    @Override
    @Transactional
    public Wallet execute(Wallet wallet, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }
    
}
