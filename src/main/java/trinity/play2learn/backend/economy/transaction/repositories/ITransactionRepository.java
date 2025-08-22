package trinity.play2learn.backend.economy.transaction.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.transaction.models.Transaction;

public interface ITransactionRepository extends CrudRepository<Transaction, Long> {
    
}
