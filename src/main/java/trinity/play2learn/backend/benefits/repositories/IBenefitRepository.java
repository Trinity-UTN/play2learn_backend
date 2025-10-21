package trinity.play2learn.backend.benefits.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitRepository extends CrudRepository<Benefit, Long>{
    
    List<Benefit> findAllBySubjectTeacher(Teacher teacher);

    List<Benefit> findBySubjectIn(List<Subject> subjects);

    Optional<Benefit> findByIdAndDeletedAtIsNull(Long id);
}
