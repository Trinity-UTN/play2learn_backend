package trinity.play2learn.backend.economy.wallet.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.messages.EconomyMessages;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRemoveAmountService;

@Service
@AllArgsConstructor
public class WalletRemoveAmountService implements IWalletRemoveAmountService {
    
    private final IWalletRepository walletRepository;
    
    @Override
    public Wallet execute(Wallet wallet, Double amount) {
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException(EconomyMessages.NOT_ENOUGH_WALLET_MONEY_STUDENT);
        } else {
            wallet.setBalance(wallet.getBalance() - amount);
            return walletRepository.save(wallet);
        }
    }
    
}
