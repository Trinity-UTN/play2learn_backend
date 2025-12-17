package trinity.play2learn.backend.admin.student.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import trinity.play2learn.backend.admin.student.models.Student;

@Repository
public interface IStudentRepository extends CrudRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    
    List<Student> findByCourseId(Long courseId); // Buscar estudiantes por ID de curso

    Optional<Student> findByIdAndDeletedAtIsNull (Long id); // Buscar estudiante por ID, asegurando que no esté eliminado

    boolean existsByDni (String dni); // Verificar si existe un estudiante por DNI, asegurando que no esté eliminado

    Optional<Student> findByIdAndDeletedAtIsNotNull(Long id);

    boolean existsByCourseId(Long courseId);

    Optional<Student> findByUserEmailAndDeletedAtIsNull(String email);

    // Método exclusivo para operaciones críticas de concurrencia
    // Esto hace un "SELECT ... FOR UPDATE" en la base de datos
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Student s WHERE s.user.email = :email AND s.deletedAt IS NULL")
    Optional<Student> findByUserEmailForUpdateAndDeletedAtIsNull(String email);

    int countByDeletedAtIsNull();
}

