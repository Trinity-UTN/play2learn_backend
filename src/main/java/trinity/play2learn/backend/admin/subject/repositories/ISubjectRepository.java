package trinity.play2learn.backend.admin.subject.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ISubjectRepository extends CrudRepository<Subject , Long> {

    Optional<Subject> findByIdAndDeletedAtIsNull(Long id);
    
    Optional<Subject> findByIdAndDeletedAtIsNotNull(Long id);
    
    Boolean existsByNameAndCourse(String name, Course course);

    Boolean existsByNameAndCourseAndIdNot(String name, Course course , Long id);

    List<Subject> findAllByDeletedAtIsNull();

    boolean existsByTeacher(Teacher teacher);

    boolean existsByCourse(Course course);

    List<Subject> findAllByTeacherAndDeletedAtIsNull(Teacher teacher);
}
