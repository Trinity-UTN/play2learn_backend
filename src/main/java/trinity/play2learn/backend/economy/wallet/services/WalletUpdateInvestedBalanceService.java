package trinity.play2learn.backend.economy.wallet.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.investment.services.interfaces.IInvestmentCalculateTotalInvestedService;

@Service
@AllArgsConstructor
public class WalletUpdateInvestedBalanceService implements IWalletUpdateInvestedBalanceService {

    private final IWalletRepository walletRepository;

    private final IInvestmentCalculateTotalInvestedService investmentCalculateTotalInvestedService;
    
    @Override
    public void execute(Wallet wallet) {
        wallet.setInvertedBalance(investmentCalculateTotalInvestedService.cu110calculateTotalInvested(wallet));
        walletRepository.save(wallet);
    }
    
}
