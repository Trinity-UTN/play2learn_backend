package trinity.play2learn.backend.economy.wallet.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface IWalletRepository extends CrudRepository<Wallet, Long> {
    
}
