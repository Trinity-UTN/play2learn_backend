package trinity.play2learn.backend.economy.reserve.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.reserve.models.Reserve;

public interface IReserveRepository extends CrudRepository<Reserve, Long> {
    
    Optional<Reserve> findFirstByOrderByCreatedAtDesc();

}
