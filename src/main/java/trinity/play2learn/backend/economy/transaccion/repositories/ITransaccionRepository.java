package trinity.play2learn.backend.economy.transaccion.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.transaccion.models.Transaccion;

public interface ITransaccionRepository extends CrudRepository<Transaccion, Long> {
    
}
