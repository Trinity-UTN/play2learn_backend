package trinity.play2learn.backend.activity.clasificacion.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.clasificacion.models.ClasificacionActivity;

public interface IClasificacionActivityRepository extends CrudRepository< ClasificacionActivity, Long>{
    
}
