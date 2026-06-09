package trinity.play2learn.backend.admin.subject.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ISubjectRepository extends CrudRepository<Subject , Long> {

    Optional<Subject> findByIdAndDeletedAtIsNull(Long id);
    
    Optional<Subject> findByIdAndDeletedAtIsNotNull(Long id);
    
    Boolean existsByNameIgnoreCaseAndCourse(String name, Course course);

    Boolean existsByNameIgnoreCaseAndCourseAndIdNot(String name, Course course , Long id);

    List<Subject> findAllByDeletedAtIsNull();

    boolean existsByTeacher(Teacher teacher);

    boolean existsByCourse(Course course);

    List<Subject> findAllByTeacherAndDeletedAtIsNull(Teacher teacher);

    List<Subject> findAllByCourseAndOptionalIsFalseAndDeletedAtIsNull(Course course);

    List<Subject> findAllByStudentsContainingAndDeletedAtIsNull(Student student);

    List<Subject> findByTeacher(Teacher teacher);

    @Query("""
        SELECT DISTINCT s FROM Subject sub
        JOIN sub.students s
        WHERE sub.id = :subjectId
        AND s.deletedAt IS NULL
        """)
    List<Student> findStudentsBySubjectId(@Param("subjectId") Long subjectId);

    @Query(value = """
            SELECT subject_id, COUNT(student_id)
            FROM subject_students
            WHERE subject_id IN :subjectIds
            GROUP BY subject_id
            """, nativeQuery = true)
    List<Object[]> countStudentsGroupedBySubjectId(@Param("subjectIds") List<Long> subjectIds);
}
