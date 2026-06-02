package trinity.play2learn.backend.activity.activity.services.student;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityStudentResultsResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetLastCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStudentResultsService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityGetStudentResultsService implements IActivityGetStudentResultsService {
    
    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityGetLastCompletedService activityGetLastCompletedService;
    private final IActivityGetByIdService activityGetByIdService;
    private final IActivityCountCompletedService activityCountCompletedService;

    @Override
    @Transactional(readOnly = true)
    public ActivityStudentResultsResponseDto cu125GetStudentResults(Long activityId, User user) {
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Activity activity = activityGetByIdService.findActivityById(activityId);

        ActivityCompleted activityCompleted = activityGetLastCompletedService.getLastCompleted(activity, student);

        int attempts = activityCountCompletedService.countCompletedByActivityAndStudent(activity, student);

        return ActivityCompletedMapper.toStudentResultsDto(activityCompleted, attempts);
    }
}
