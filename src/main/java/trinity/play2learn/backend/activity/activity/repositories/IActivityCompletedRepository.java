package trinity.play2learn.backend.activity.activity.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityCompletedRepository extends CrudRepository<ActivityCompleted, Long> {
    
    //Trae la ultima actividad completada de un estudiante por actividad
    Optional<ActivityCompleted> findTopByActivityAndStudentOrderByCompletedAtDesc(Activity activity, Student student); 
}
