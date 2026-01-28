package trinity.play2learn.backend.activity.activity.services.student;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentStateResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListApproveByStudentService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityListApprovedByStudentService implements IActivityListApproveByStudentService {

    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityGetByStudentService activityGetByStudentService;
    private final IActivityCreateApprovedDtosService activityCreateApprovedDtosService;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityStudentStateResponseDto> cu63ListApprovedActivitiesByStudent(User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        // Obtengo todas las actividades del estudiante segun las materias a las que
        // esta asignado
        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        return activityCreateApprovedDtosService.createApprovedDtos(activities, student);
    }

}
