package trinity.play2learn.backend.economy.wallet.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetTotalRakingService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;

@Service
@AllArgsConstructor
public class WalletGetTotalRankingService implements IWalletGetTotalRakingService {

    private final IWalletRepository walletRepository;

    @Override
    public List<Wallet> execute() {
        return walletRepository.findTop10OrderByTotalBalanceDesc();
    }
    
}
