package trinity.play2learn.backend.admin.student.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IStudentRepository extends CrudRepository<Student, Long> {
    
}
