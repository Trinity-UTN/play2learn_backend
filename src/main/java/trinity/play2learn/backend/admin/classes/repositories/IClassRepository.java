package trinity.play2learn.backend.admin.classes.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import trinity.play2learn.backend.admin.classes.models.Class;
import trinity.play2learn.backend.admin.year.models.Year;

@Repository
public interface IClassRepository extends CrudRepository<Class, Long> {

    /**
     * Busca un año por su nombre.
     *
     * @param name Nombre del año.
     * @return Year encontrado.
     */
    Optional<Year> findByName(String name);
    /**
     * Busca si existe una clase por su nombre y año.
     *
     * @param name Nombre del año.
     * @param year Año de la clase.
     * @return boolean.
     */
    boolean existsByNameAndYear(String name, Year year);
    /**
     * Busca si existe una clase por su nombre.
     *
     * @param name Nombre del año.
     * @return boolean.
     */
    boolean existsByName(String name);

    
}
