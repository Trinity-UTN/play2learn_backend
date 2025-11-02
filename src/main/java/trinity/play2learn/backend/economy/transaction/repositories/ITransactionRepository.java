package trinity.play2learn.backend.economy.transaction.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface ITransactionRepository extends CrudRepository<Transaction, Long> {

    public List<Transaction> findTop10ByWalletOrderByCreatedAtDesc(Wallet wallet);

    List<Transaction> findAllByOrderByCreatedAtAsc();
    
}
