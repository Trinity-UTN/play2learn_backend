package trinity.play2learn.backend.admin.teacher.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ITeacherPaginatedRepository extends CrudRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {
    
}
