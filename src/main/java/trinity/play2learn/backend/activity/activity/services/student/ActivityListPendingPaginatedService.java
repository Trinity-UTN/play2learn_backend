package trinity.play2learn.backend.activity.activity.services.student;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentStateResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListPendingPaginatedService;
import trinity.play2learn.backend.activity.activity.specs.ActivityCompletedSpecs;
import trinity.play2learn.backend.activity.activity.specs.ActivitySpecs;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityListPendingPaginatedService implements IActivityListPendingPaginatedService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<ActivityStudentStateResponseDto> cu131ListPendingActivitiesByStudentPaginated(
            int page,
            int size,
            String orderBy,
            String orderType,
            String search,
            List<String> filters,
            List<String> filterValues,
            User user) {
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);

        Specification<ActivityCompleted> spec = Specification.where(ActivityCompletedSpecs.filterByStudent(student));
        spec = spec.and(ActivityCompletedSpecs.filterByState(ActivityCompletedState.PENDING));

        if (search != null && !search.isBlank()) {
            spec = spec.and(ActivityCompletedSpecs.nameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                String field = filters.get(i);
                String value = filterValues.get(i);
                spec = spec.and(ActivityCompletedSpecs.genericFilter(field, value));
            }
        }

        Page<ActivityCompleted> pageResult = activityCompletedRepository.findAll(spec, pageable);

        List<ActivityStudentStateResponseDto> dtos = pageResult.getContent().stream()
                .map(activityCompleted -> ActivityMapper.toStudentStateDto(activityCompleted.getActivity(),
                        activityCompleted.getRemainingAttempts(), 0.0,
                        activityCompleted.getCompletedAt(), activityCompleted.getState()))
                .collect(Collectors.toList());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
}
