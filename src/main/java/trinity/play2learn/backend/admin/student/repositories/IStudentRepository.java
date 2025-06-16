package trinity.play2learn.backend.admin.student.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import trinity.play2learn.backend.admin.student.models.Student;

@Repository
public interface IStudentRepository extends CrudRepository<Student, Long> {
    
}
