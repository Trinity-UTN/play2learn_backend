package trinity.play2learn.backend.admin.year.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.year.models.Year;

/**
 * Repositorio JPA para acceder a los datos persistidos del modelo Year.
 */
public interface IYearRepository extends CrudRepository<Year, Long> {

    /**
     * Busca un año por su nombre.
     *
     * @param name Nombre del año.
     * @return Year encontrado.
     */
    Optional<Year> findByName(String name);
} 
