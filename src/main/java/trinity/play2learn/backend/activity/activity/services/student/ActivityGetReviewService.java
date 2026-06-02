package trinity.play2learn.backend.activity.activity.services.student;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetReviewService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityReviewResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityGetReviewService implements IActivityGetReviewService {
    
    private final IStudentGetByEmailService studentGetByEmailService;

    private final IActivityGetByIdService activityGetByIdService;

    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public ActivityReviewResponseDto cu130GetReview(
        User user,
        Long activityId) {
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Activity activity = activityGetByIdService.findActivityById(activityId);

        Optional<ActivityCompleted> activityCompleted = activityCompletedRepository.findTopByActivityAndStudentAndNotStateOrderByCompletedAtDesc(activity, student, ActivityCompletedState.PENDING);
        
        if (activityCompleted.isEmpty()) {
            throw new ConflictException("No se encontró un intento de realizacion que este en estado aprobado o desaprobado");
        }

        return ActivityCompletedMapper.toReviewDto(activityCompleted.get());
    }
}
