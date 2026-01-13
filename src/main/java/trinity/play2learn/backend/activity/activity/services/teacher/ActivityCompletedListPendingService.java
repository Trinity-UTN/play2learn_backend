package trinity.play2learn.backend.activity.activity.services.teacher;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedPendingDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedListPendingService;
import trinity.play2learn.backend.activity.activity.specs.ActivityCompletedSpecs;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityCompletedListPendingService implements IActivityCompletedListPendingService {
    
    private final ITeacherGetByEmailService teacherGetByEmailService;

    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityCompletedPendingDto> cu128ListPendingActivities(User user) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        //Trae todas las actividades completadas cuya actividad pertenezca a un docente
        List<ActivityCompleted> pendingActivitiesCompleted = activityCompletedRepository.findByStateAndActivity_Subject_Teacher(
            ActivityCompletedState.PENDING, teacher);
        
        return ActivityCompletedMapper.toPendingDtoList(pendingActivitiesCompleted);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<ActivityCompletedPendingDto> cu128ListPendingActivitiesPaginated(
        int page, 
        int size, 
        String orderBy, 
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues, 
        User user
    ) {
        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        
        Specification<ActivityCompleted> spec = Specification.where(ActivityCompletedSpecs.filterByTeacher(teacher));
        spec = spec.and(ActivityCompletedSpecs.filterByState(ActivityCompletedState.PENDING));

        Page<ActivityCompleted> pageResult = activityCompletedRepository.findAll(spec, pageable);

        List<ActivityCompletedPendingDto> dtos = ActivityCompletedMapper.toPendingDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos); 
    }
}
