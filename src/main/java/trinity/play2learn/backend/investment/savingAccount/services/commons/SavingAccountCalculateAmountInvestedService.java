package trinity.play2learn.backend.investment.savingAccount.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountCalculateAmountInvestedService;

@Service
@AllArgsConstructor
public class SavingAccountCalculateAmountInvestedService implements ISavingAccountCalculateAmountInvestedService {
    
    private final ISavingAccountRepository savingAccountRepository;

    @Override
    public Double execute(Wallet wallet) {
        
        Double total = 0.0;
        
        for (SavingAccount savingAccount : savingAccountRepository.findAllByWalletAndDeletedAtIsNull(wallet)) {
            total += savingAccount.getCurrentAmount();
        }
        
        return total;
    }
    
}
