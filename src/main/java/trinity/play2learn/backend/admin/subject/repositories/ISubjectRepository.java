package trinity.play2learn.backend.admin.subject.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.subject.models.Subject;

public interface ISubjectRepository extends CrudRepository<Subject , Long> {

    Optional<Subject> findByIdAndDeletedAtIsNull(Long id);
    
    Boolean existsByNameAndCourse(String name, Course course);
}
