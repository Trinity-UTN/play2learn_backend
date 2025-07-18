package trinity.play2learn.backend.admin.student.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import trinity.play2learn.backend.admin.student.models.Student;

@Repository
public interface IStudentRepository extends CrudRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    
    List<Student> findByCourseId(Long courseId); // Buscar estudiantes por ID de curso

    Optional<Student> findByIdAndDeletedAtIsNull (Long id); // Buscar estudiante por ID, asegurando que no esté eliminado

    boolean existsByDni (String dni); // Verificar si existe un estudiante por DNI, asegurando que no esté eliminado

    Optional<Student> findByIdAndDeletedAtIsNotNull(Long id);
}

