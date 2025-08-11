package trinity.play2learn.backend.benefits.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitRepository extends CrudRepository<Benefit, Long>{
    
    List<Benefit> findAllBySubjectTeacher(Teacher teacher);
}
