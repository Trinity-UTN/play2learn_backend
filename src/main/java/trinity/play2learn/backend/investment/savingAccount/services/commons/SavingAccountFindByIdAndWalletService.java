package trinity.play2learn.backend.investment.savingAccount.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountFindByIdAndWalletService;

@Service
@AllArgsConstructor
public class SavingAccountFindByIdAndWalletService implements ISavingAccountFindByIdAndWalletService{
    
    private final ISavingAccountRepository savingAccountRepository;

    @Override
    public SavingAccount execute(Long id, Wallet wallet) {
        return savingAccountRepository.findByIdAndWallet(id, wallet);
    }
    
}
