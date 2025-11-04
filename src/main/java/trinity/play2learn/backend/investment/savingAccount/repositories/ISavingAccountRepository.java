package trinity.play2learn.backend.investment.savingAccount.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

public interface ISavingAccountRepository extends CrudRepository<SavingAccount, Long>, JpaSpecificationExecutor<SavingAccount> {
    
    boolean existsByNameAndWalletAndDeletedAtIsNull(String name, Wallet wallet);

    SavingAccount findByIdAndWalletAndDeletedAtIsNull(Long id, Wallet wallet);

    List<SavingAccount> findAllByDeletedAtIsNull();

    
}
