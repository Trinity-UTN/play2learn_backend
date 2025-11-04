package trinity.play2learn.backend.investment.savingAccount.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountExistsByNameAndWalletService;


@Service
@AllArgsConstructor
public class SavingAccountExistsByNameAndWalletService implements ISavingAccountExistsByNameAndWalletService {
    
    private final ISavingAccountRepository savingAccountRepository;
    
    
    @Override
    public boolean execute(String name, Wallet wallet) {
        return savingAccountRepository.existsByNameAndWallet(name, wallet);
    }

}
