package trinity.play2learn.backend.admin.course.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.year.models.Year;

@Repository
public interface ICourseRepository extends CrudRepository<Course, Long> {

    /**
     * Busca un año por su nombre.
     *
     * @param name Nombre del año.
     * @return Year encontrado.
     */
    Optional<Year> findByName(String name);
    /**
     * Busca si existe un curso por su nombre y año.
     *
     * @param name Nombre del año.
     * @param year Año de la clase.
     * @return boolean.
     */
    boolean existsByNameAndYear(String name, Year year);
    /**
     * Busca si existe un curso por su nombre.
     *
     * @param name Nombre del año.
     * @return boolean.
     */
    boolean existsByName(String name);
    
}
