package trinity.play2learn.backend.activity.activity.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherSimpleDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateTeacherSimpleDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListByTeacherPaginatedService;
import trinity.play2learn.backend.activity.activity.specs.ActivitySpecs;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityListByTeacherPaginatedService implements IActivityListByTeacherPaginatedService {

    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IActivityPaginatedRepository activityRepository;
    private final IActivityCreateTeacherSimpleDtosService activityCreateTeacherSimpleDtosService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<ActivityTeacherSimpleDto> cu111ListActivityByTeacherPaginated(int page, int size,
            String orderBy, String orderType, String search, List<String> filters, List<String> filterValues,
            User user) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);

        Specification<Activity> spec = Specification.where(ActivitySpecs.notDeleted());

        spec = spec.and(ActivitySpecs.filterByTeacher(teacher)); //Filtro actividades solo del docente

        if (search != null && !search.isBlank()) {
            spec = spec.and(ActivitySpecs.nameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                String field = filters.get(i);
                String value = filterValues.get(i);

                spec = spec.and(ActivitySpecs.genericFilter(field, value));

            }
        }

        Page<Activity> pageResult = activityRepository.findAll(spec, pageable);

        List<ActivityTeacherSimpleDto> dtos = activityCreateTeacherSimpleDtosService.createTeacherSimpleDtos(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
}
