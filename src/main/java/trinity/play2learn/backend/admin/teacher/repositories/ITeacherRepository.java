package trinity.play2learn.backend.admin.teacher.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ITeacherRepository extends CrudRepository<Teacher,Long> {
    
}
