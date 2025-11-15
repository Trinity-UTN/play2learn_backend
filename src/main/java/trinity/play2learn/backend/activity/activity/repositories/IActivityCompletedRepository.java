package trinity.play2learn.backend.activity.activity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityCompletedRepository extends CrudRepository<ActivityCompleted, Long> {
    
    //Trae la ultima actividad completada de un estudiante por actividad
    Optional<ActivityCompleted> findTopByActivityAndStudentOrderByCompletedAtDesc(Activity activity, Student student); 

    Optional<ActivityCompleted> findTopByActivityAndStudentAndStateOrderByStartedAtDesc(Activity activity, Student student, ActivityCompletedState state);

    int countByActivityAndState (Activity activity, ActivityCompletedState state);

    int countByActivity (Activity activity);

    List<ActivityCompleted> findTop5ByStudentAndStateNotOrderByCompletedAtDesc(Student student, ActivityCompletedState state);

    List<ActivityCompleted> findAllByActivity(Activity activity);

}
