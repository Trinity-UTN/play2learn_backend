package trinity.play2learn.backend.activity.activity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface IActivityCompletedRepository extends CrudRepository<ActivityCompleted, Long> {

    //Trae todas las actividades completadas cuya actividad pertenezca a un docente
    List<ActivityCompleted> findByStateAndActivity_Subject_Teacher(ActivityCompletedState state, Teacher teacher);

    //Trae la ultima actividad completada de un estudiante por actividad
    Optional<ActivityCompleted> findTopByActivityAndStudentOrderByCompletedAtDesc(Activity activity, Student student); 

    Optional<ActivityCompleted> findTopByActivityAndStudentAndStateOrderByStartedAtDesc(Activity activity, Student student, ActivityCompletedState state);
    
    Optional<ActivityCompleted> findTopByActivityAndStudentOrderByStartedAtDesc(Activity activity, Student student);

    int countByActivityAndState (Activity activity, ActivityCompletedState state);

    int countByActivity (Activity activity);

    int countByActivityAndStudent(Activity activity, Student student);
    
    int countByStudentAndState(Student student, ActivityCompletedState state);
    
    List<ActivityCompleted> findTop5ByStudentAndStateNotOrderByCompletedAtDesc(Student student, ActivityCompletedState state);

    List<ActivityCompleted> findAllByActivity(Activity activity);

    @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
            "FROM Student s " +
            "WHERE (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > 0 " +
            "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC LIMIT 10")
    List<Object[]> findTop10StudentsByApprovedActivities(ActivityCompletedState state);
    
    @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
            "FROM Student s " +
            "WHERE (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > 0 " +
            "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC")
    List<Object[]> findAllStudentsByApprovedActivities(ActivityCompletedState state);
   
    @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
            "FROM Student s WHERE s IN :students " +
            "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC LIMIT 10")
    List<Object[]> findTop10StudentsByApprovedActivitiesInStudentList(List<Student> students, ActivityCompletedState state);
    
    @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
            "FROM Student s WHERE s IN :students " +
            "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC")
    List<Object[]> findAllStudentsByApprovedActivitiesInStudentList(List<Student> students, ActivityCompletedState state);

    @Query("SELECT " +
            "(SELECT COUNT(DISTINCT s) + 1 FROM Student s WHERE " +
            "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > " +
            "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) " +
            "OR ((SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) = " +
            "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) " +
            "AND s.id < (SELECT st.id FROM Student st WHERE st = :targetStudent))), " +
            "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) " +
            "FROM Student st WHERE st = :targetStudent")
    Object[] findPositionByStudentInApprovedActivities(Student targetStudent, ActivityCompletedState state);

    @Query("SELECT " +
            "(SELECT COUNT(DISTINCT s) + 1 FROM Student s WHERE s IN :students AND " +
            "((SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > " +
            "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) " +
            "OR ((SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) = " +
            "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) " +
            "AND s.id < (SELECT st.id FROM Student st WHERE st = :targetStudent)))), " +
            "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) " +
            "FROM Student st WHERE st = :targetStudent")
    Object[] findPositionByStudentInApprovedActivitiesInList(Student targetStudent, List<Student> students, ActivityCompletedState state);

}
