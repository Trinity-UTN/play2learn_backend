package trinity.play2learn.backend.activity.activity.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListNotApprovedByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityListNotApprovedByStudentService implements IActivityListNotApprovedByStudentService {
    
    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityGetByStudentService activityGetByStudentService;
    private final IActivityCreateNotApprovedDtosService activityCreateNotApprovedDtosService;

    //Devuelve las activdades que aun no han sido aprobadas por el estudiante
    @Override
    @Transactional(readOnly = true)
    public List<ActivityStudentNotApprovedResponseDto> cu62ListNotApprovedActivitiesByStudent(User user) { 

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        //Obtengo todas las actividades del estudiante segun las materias a las que esta asignado
        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        //Filtra las actividades que aun no han sido aprobadas y crea los dtos
        return activityCreateNotApprovedDtosService.createNotApprovedDtos(activities, student);
    }
        
}
 