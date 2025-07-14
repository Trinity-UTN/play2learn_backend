package trinity.play2learn.backend.admin.teacher.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ITeacherRepository extends CrudRepository<Teacher,Long> {
    
    Optional<Teacher> findByIdAndDeletedAtIsNull(Long id);

    List<Teacher> findAllByDeletedAtIsNull();
}
