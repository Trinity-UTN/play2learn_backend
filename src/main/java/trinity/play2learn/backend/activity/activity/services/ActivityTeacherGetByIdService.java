package trinity.play2learn.backend.activity.activity.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentGetResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherGetResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityAverageCompletionTimeService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateRewardStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateStudentGetDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStatusService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStudentsApprovedCountService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStudentsAttemptedCountService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityTeacherGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityTeacherGetByIdService implements IActivityTeacherGetByIdService {
    
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IActivityGetByIdService activityGetByIdService;
    private final IActivityStudentsAttemptedCountService activityStudentsAttemptedCountService;
    private final IActivityStudentsApprovedCountService activityStudentsApprovedCountService;
    private final IActivityAverageCompletionTimeService activityAverageCompletionTimeService;
    private final IActivityCreateStudentGetDtosService activityCreateStudentGetDtosService;
    private final Map<String, IActivityCalculateRewardStrategyService> activityCalculateRewardStrategyServiceMap;
    private final IActivityGetStatusService activityGetStatusService;

    @Override
    @Transactional(readOnly = true)
    public ActivityTeacherGetResponseDto cu112TeacherGetActivityById(User user, Long id) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Activity activity = activityGetByIdService.findActivityById(id); 

        if (!activity.getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException("El docente no esta asignado a la materia de la actividad");
        }

        int studentsAttemptedCount = activityStudentsAttemptedCountService.getActivityStudentsAttemptedCount(activity);
        int studentsApprovedCount = activityStudentsApprovedCountService.activityGetStudentsApprovedCount(activity);
        Double averageCompletionTime = Math.round( activityAverageCompletionTimeService.activityGetAverageCompletionTime(activity) * 100.0) / 100.0;
        Double participationPercentage = Math.round((double) studentsAttemptedCount / activity.getSubject().getStudents().size() * 100 * 100.0) / 100.0;
        Double successPercentage = Math.round((studentsApprovedCount * 100.0 / studentsAttemptedCount) * 100.0) / 100.0;

        List<ActivityStudentGetResponseDto> activityStudentGetDtos = activityCreateStudentGetDtosService.createStudentGetDtos(activity);

        //Recompensa que obtiene cada estudiante por completar la actividad
        Double reward = activityCalculateRewardStrategyServiceMap.get(activity.getTypeReward().name()).execute(activity);
        
        ActivityStatus status = activityGetStatusService.getStatus(activity);

        return ActivityMapper.toTeacherGetDto(
            activity,status, studentsAttemptedCount, studentsApprovedCount, averageCompletionTime, 
            participationPercentage, successPercentage, activityStudentGetDtos, reward);
    }
}
