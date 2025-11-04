package trinity.play2learn.backend.investment.savingAccount.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;

public interface ISavingAccountRepository extends CrudRepository<SavingAccount, Long>, JpaSpecificationExecutor<SavingAccount> {
    
    boolean existsByNameAndWalletAndDeletedAtIsNull(String name, Wallet wallet);

    Optional<SavingAccount> findByIdAndDeletedAtIsNull(Long id);

    List<SavingAccount> findAllByDeletedAtIsNull();

    
}
