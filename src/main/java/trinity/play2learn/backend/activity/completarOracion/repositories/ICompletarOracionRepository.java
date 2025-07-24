package trinity.play2learn.backend.activity.completarOracion.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.completarOracion.models.CompletarOracionActivity;

public interface ICompletarOracionRepository extends CrudRepository<CompletarOracionActivity, Long>{
    
}
