package trinity.play2learn.backend.admin.course.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.year.models.Year;

@Repository
public interface ICourseRepository extends CrudRepository<Course, Long>, JpaSpecificationExecutor<Course> {

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
    boolean existsByNameIgnoreCaseAndYear(String name, Year year);
    /**
     * Busca si existe un curso por su nombre.
     *
     * @param name Nombre del año.
     * @return boolean.
     */
    boolean existsByName(String name);

    /**
     * Buscar todos los cursos que no han sido eliminados.
     *
     * @return Iterable de cursos.
     */
    Iterable<Course> findAllByDeletedAtIsNull();


    /**
     * Verifica si existe un curso por su ID y que no haya sido eliminado.
     *
     * @param id ID del curso.
     * @return boolean.
     */
    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.year.id = :yearId AND c.deletedAt IS NULL")
    boolean existsByYearIdAndDeletedAtIsNull(@Param("yearId") Long yearId);

    Optional<Course> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameIgnoreCaseAndYearAndIdNot(String name, Year year , Long id);

    int countByDeletedAtIsNull();

}
