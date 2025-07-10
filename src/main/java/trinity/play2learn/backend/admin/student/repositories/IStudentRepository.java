package trinity.play2learn.backend.admin.student.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import trinity.play2learn.backend.admin.student.models.Student;

@Repository
public interface IStudentRepository extends CrudRepository<Student, Long> {
    
    List<Student> findByCourseId(Long courseId); // Buscar estudiantes por ID de curso
}
