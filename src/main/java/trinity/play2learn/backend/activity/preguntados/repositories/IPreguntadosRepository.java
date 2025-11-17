package trinity.play2learn.backend.activity.preguntados.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.preguntados.models.Preguntados;

public interface IPreguntadosRepository extends CrudRepository<Preguntados, Long> {
        
}
