package trinity.play2learn.backend.economy.wallet.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletAddAmountService;

@Service
@AllArgsConstructor
public class WalletAddAmountService implements IWalletAddAmountService {

    private final IWalletRepository walletRepository;

    @Override
    @Transactional
    public Wallet execute(Wallet wallet, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(EconomyMessages.AMOUNT_MAJOR_TO_0);
        }
        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }
    
}
