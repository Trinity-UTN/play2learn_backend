package trinity.play2learn.backend.investment.savingAccount.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

public interface ISavingAccountRepository extends CrudRepository<SavingAccount, Long>, JpaSpecificationExecutor<SavingAccount> {
    
    boolean existsByNameAndWallet(String name, Wallet wallet);

    SavingAccount findByIdAndWallet(Long id, Wallet wallet);

    
}
