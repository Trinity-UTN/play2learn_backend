package trinity.play2learn.backend.activity.activity.services.student;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNotApprovedListPaginatedService;
import trinity.play2learn.backend.activity.activity.specs.ActivitySpecs;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityNotApprovedListPaginatedService implements IActivityNotApprovedListPaginatedService {

    private final IActivityPaginatedRepository activityRepository;
    private final IActivityCompletedRepository activityCompletedRepository;
    private final IActivityCreateNotApprovedDtosService activityCreateNotApprovedDtosService;
    private final IStudentGetByEmailService studentGetByEmailService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<ActivityStudentNotApprovedResponseDto> cu66listNotApprovedActivitiesPaginated(
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

        Specification<Activity> spec = Specification.where(ActivitySpecs.belongsToStudent(student));
        spec = spec.and(ActivitySpecs.notApprovedNorPendingForStudent(student));

        if (search != null && !search.isBlank()) {
            spec = spec.and(ActivitySpecs.nameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                String field = filters.get(i);
                String value = filterValues.get(i);

                if (field.equals("disapproved")) {
                    spec = spec.and(ActivitySpecs.filterByDisapprovedForStudent(student, Boolean.parseBoolean(value)));
                } else {
                    spec = spec.and(ActivitySpecs.genericFilter(field, value));
                }
            }
        }

        Page<Activity> pageResult = activityRepository.findAll(spec, pageable);

        List<Activity> activities = pageResult.getContent();
        List<Long> activityIds = activities.stream().map(Activity::getId).toList();

        Map<Long, ActivityCompleted> latestCompletionByActivityId = loadLatestCompletions(student, activityIds);
        Set<Long> activityIdsInProgress = loadActivityIdsInProgress(student, activityIds);

        List<ActivityStudentNotApprovedResponseDto> dtos = activityCreateNotApprovedDtosService
                .createNotApprovedDtos(activities, student, latestCompletionByActivityId, activityIdsInProgress);

        return PaginationHelper.fromPage(pageResult, dtos);
    }

    private Map<Long, ActivityCompleted> loadLatestCompletions(Student student, List<Long> activityIds) {
        if (activityIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return activityCompletedRepository.findLatestByStudentAndActivityIds(student, activityIds).stream()
                .collect(Collectors.toMap(ac -> ac.getActivity().getId(), ac -> ac, (first, second) -> first));
    }

    private Set<Long> loadActivityIdsInProgress(Student student, List<Long> activityIds) {
        if (activityIds.isEmpty()) {
            return Collections.emptySet();
        }

        return Set.copyOf(activityCompletedRepository.findActivityIdsByStudentAndActivityIdsAndState(
                student, activityIds, ActivityCompletedState.IN_PROGRESS));
    }
}
