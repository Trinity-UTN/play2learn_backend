package trinity.play2learn.backend.activity.activity.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;

public interface IActivityCompletedRepository
                extends CrudRepository<ActivityCompleted, Long>, JpaSpecificationExecutor<ActivityCompleted> {

        // Trae todas las actividades completadas cuya actividad pertenezca a un docente
        List<ActivityCompleted> findByStateAndActivity_Subject_Teacher(ActivityCompletedState state, Teacher teacher);

        // Trae la ultima actividad completada de un estudiante por actividad
        Optional<ActivityCompleted> findTopByActivityAndStudentOrderByCompletedAtDesc(Activity activity,
                        Student student);

        Optional<ActivityCompleted> findTopByActivityAndStudentAndStateOrderByStartedAtDesc(Activity activity,
                        Student student, ActivityCompletedState state);

        Optional<ActivityCompleted> findTopByActivityAndStudentOrderByStartedAtDesc(Activity activity, Student student);

        int countByActivityAndState(Activity activity, ActivityCompletedState state);

        int countByActivity(Activity activity);

        int countByActivityAndStudent(Activity activity, Student student);

        int countByStudentAndState(Student student, ActivityCompletedState state);

        List<ActivityCompleted> findTop5ByStudentAndStateNotOrderByCompletedAtDesc(Student student,
                        ActivityCompletedState state);

        List<ActivityCompleted> findAllByActivity(Activity activity);

        @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
                        "FROM Student s " +
                        "WHERE (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > 0 "
                        +
                        "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC LIMIT 10")
        List<Object[]> findTop10StudentsByApprovedActivities(ActivityCompletedState state);

        @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
                        "FROM Student s " +
                        "WHERE (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > 0 "
                        +
                        "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC")
        List<Object[]> findAllStudentsByApprovedActivities(ActivityCompletedState state);

        @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
                        "FROM Student s WHERE s IN :students " +
                        "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC LIMIT 10")
        List<Object[]> findTop10StudentsByApprovedActivitiesInStudentList(List<Student> students,
                        ActivityCompletedState state);

        @Query("SELECT s, (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) " +
                        "FROM Student s WHERE s IN :students " +
                        "ORDER BY (SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) DESC, s.id ASC")
        List<Object[]> findAllStudentsByApprovedActivitiesInStudentList(List<Student> students,
                        ActivityCompletedState state);

        @Query("SELECT " +
                        "(SELECT COUNT(DISTINCT s) + 1 FROM Student s WHERE " +
                        "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > " +
                        "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) "
                        +
                        "OR ((SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) = "
                        +
                        "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) "
                        +
                        "AND s.id < (SELECT st.id FROM Student st WHERE st = :targetStudent))), " +
                        "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) "
                        +
                        "FROM Student st WHERE st = :targetStudent")
        Object[] findPositionByStudentInApprovedActivities(Student targetStudent, ActivityCompletedState state);

        @Query("SELECT " +
                        "(SELECT COUNT(DISTINCT s) + 1 FROM Student s WHERE s IN :students AND " +
                        "((SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) > " +
                        "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) "
                        +
                        "OR ((SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = s AND ac.state = :state) = "
                        +
                        "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) "
                        +
                        "AND s.id < (SELECT st.id FROM Student st WHERE st = :targetStudent)))), " +
                        "(SELECT COUNT(ac) FROM ActivityCompleted ac WHERE ac.student = :targetStudent AND ac.state = :state) "
                        +
                        "FROM Student st WHERE st = :targetStudent")
        Object[] findPositionByStudentInApprovedActivitiesInList(Student targetStudent, List<Student> students,
                        ActivityCompletedState state);

        // Trae el ultimo intento de realizacion que no este en el estado pasado por
        // parametro
        @Query("SELECT ac FROM ActivityCompleted ac WHERE ac.activity = :activity AND ac.student = :student AND ac.state != :state ORDER BY ac.completedAt DESC")
        Optional<ActivityCompleted> findTopByActivityAndStudentAndNotStateOrderByCompletedAtDesc(
                        Activity activity,
                        Student student,
                        ActivityCompletedState state);

        // Trae la suma de las recompensas de un estudiante por materia y estado (Para
        // el ranking de monedas por materia)
        @Query("SELECT COALESCE(SUM(ac.reward), 0) " +
                        "FROM ActivityCompleted ac " +
                        "WHERE ac.state = :state " +
                        "AND ac.activity.subject = :subject " +
                        "AND ac.student = :student")
        Double sumRewardBySubjectAndStateAndStudent(
                        @Param("subject") Subject subject,
                        @Param("state") ActivityCompletedState state,
                        @Param("student") Student student);

        // Trae la suma de las recompensas de un estudiante por estado (Para el ranking
        // de monedas por institucion y curso)
        @Query("SELECT COALESCE(SUM(ac.reward), 0) " +
                        "FROM ActivityCompleted ac " +
                        "WHERE ac.state = :state " +
                        "AND ac.student = :student")
        Double sumRewardByStateAndStudent(
                        @Param("state") ActivityCompletedState state,
                        @Param("student") Student student);

        @Query("SELECT new trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto(ac.student, SUM(ac.reward)) "
                        +
                        "FROM ActivityCompleted ac " +
                        "WHERE ac.state = :state " +
                        "GROUP BY ac.student")
        List<StudentWithTotalDto> sumRewardByStateGroupedByStudent(
                        @Param("state") ActivityCompletedState state);

        @Query("SELECT COUNT(ac) " +
                        "FROM ActivityCompleted ac " +
                        "WHERE ac.state = :state " +
                        "AND ac.activity.subject = :subject " +
                        "AND ac.student = :student")
        Double countByActivitySubjectStateAndStudent(
                        @Param("subject") Subject subject,
                        @Param("state") ActivityCompletedState state,
                        @Param("student") Student student);

        @Query(value = """
                        SELECT ac.* FROM activity_completed ac
                        WHERE ac.student_id = :#{#student.id}
                          AND ac.activity_id IN (:activityIds)
                          AND ac.completed_at IS NOT NULL
                          AND ac.id = (
                            SELECT ac_latest.id FROM activity_completed ac_latest
                            WHERE ac_latest.activity_id = ac.activity_id
                              AND ac_latest.student_id = :#{#student.id}
                              AND ac_latest.completed_at IS NOT NULL
                            ORDER BY ac_latest.completed_at DESC, ac_latest.id DESC
                            LIMIT 1
                          )
                        """, nativeQuery = true)
        List<ActivityCompleted> findLatestByStudentAndActivityIds(
                        @Param("student") Student student,
                        @Param("activityIds") List<Long> activityIds);

        @Query("SELECT DISTINCT ac.activity.id FROM ActivityCompleted ac " +
                        "WHERE ac.student = :student AND ac.activity.id IN :activityIds " +
                        "AND ac.state = :state")
        List<Long> findActivityIdsByStudentAndActivityIdsAndState(
                        @Param("student") Student student,
                        @Param("activityIds") List<Long> activityIds,
                        @Param("state") ActivityCompletedState state);

        @Query("SELECT DISTINCT ac FROM ActivityCompleted ac JOIN FETCH ac.activity a JOIN FETCH a.subject WHERE ac.id IN :ids")
        List<ActivityCompleted> findAllByIdInWithActivityAndSubject(@Param("ids") Collection<Long> ids);

        @Query(value = """
                        SELECT activity_id, COUNT(*)
                        FROM activity_completed
                        WHERE activity_id IN :activityIds AND state = :state
                        GROUP BY activity_id
                        """, nativeQuery = true)
        List<Object[]> countApprovedGroupedByActivityId(
                        @Param("activityIds") List<Long> activityIds,
                        @Param("state") int state);
}
