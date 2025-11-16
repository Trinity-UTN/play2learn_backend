package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountCompletedService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCountCompletedService implements IActivityCountCompletedService{
    
    private final IActivityCompletedRepository activityCompletedRepository;

    //Cuenta cuantos intentos realizo un estudiante de una actividad
    @Override
    public int countCompletedByActivityAndStudent(Activity activity, Student student) {
        
        return activityCompletedRepository.countByActivityAndStudent(activity, student);
    }
    
}
