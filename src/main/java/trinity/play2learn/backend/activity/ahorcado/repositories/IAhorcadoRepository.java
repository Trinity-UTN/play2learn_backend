package trinity.play2learn.backend.activity.ahorcado.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;

public interface IAhorcadoRepository extends CrudRepository<Ahorcado, Long> {
    
}
