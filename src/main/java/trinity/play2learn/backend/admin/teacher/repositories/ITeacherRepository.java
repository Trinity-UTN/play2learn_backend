package trinity.play2learn.backend.admin.teacher.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ITeacherRepository extends CrudRepository<Teacher,Long> {
    
    Optional<Teacher> findByIdAndDeletedAtIsNull(Long id);

    Optional<Teacher> findByIdAndDeletedAtIsNotNull(Long id);


    //Busca si existe un profesor distinto del pasado por id con el mismo dni.
    Boolean existsByDniAndIdNot(String dni, Long id);

    Boolean existsByDni(String dni);

    Optional<Teacher> findByUserEmailAndDeletedAtIsNull(String email);

}
