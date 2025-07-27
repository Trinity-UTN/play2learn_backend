package trinity.play2learn.backend.activity.ordenarSecuencia.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.ordenarSecuencia.models.OrdenarSecuencia;

public interface IOrdenarSecuenciaRepository extends CrudRepository<OrdenarSecuencia, Long> {
    // Aquí puedes agregar métodos específicos para manejar OrdenarSecuencia si es necesario
    // Por ejemplo, encontrar por algún campo específico o realizar consultas personalizadas
    
}
