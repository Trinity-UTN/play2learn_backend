package trinity.play2learn.backend.activity.activity.services.student;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.ActivityNotApprovedNativeRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNotApprovedListPaginatedService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityNotApprovedListPaginatedService implements IActivityNotApprovedListPaginatedService {

    private final IActivityPaginatedRepository activityRepository;
    private final ActivityNotApprovedNativeRepository activityNotApprovedNativeRepository;
    private final IActivityCompletedRepository activityCompletedRepository;
    private final ISubjectRepository subjectRepository;
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

        Page<Long> idPage = activityNotApprovedNativeRepository.findNotApprovedActivityIds(
                student, pageable, search, filters, filterValues);

        if (idPage.isEmpty()) {
            return PaginationHelper.fromPage(Page.empty(pageable), List.of());
        }

        List<Activity> activities = loadActivitiesPreservingOrder(idPage.getContent());
        Page<Activity> pageResult = new PageImpl<>(activities, pageable, idPage.getTotalElements());

        List<Long> activityIds = activities.stream().map(Activity::getId).toList();

        Map<Long, ActivityCompleted> latestCompletionByActivityId = loadLatestCompletions(student, activityIds);
        Set<Long> activityIdsInProgress = loadActivityIdsInProgress(student, activityIds);
        Map<Long, Integer> studentsCountBySubjectId = loadStudentsCountBySubjectId(activities);
        Map<Long, Integer> approvedCountByActivityId = loadApprovedCountByActivityId(activityIds);

        List<ActivityStudentNotApprovedResponseDto> dtos = activityCreateNotApprovedDtosService
                .createNotApprovedDtos(activities, student, latestCompletionByActivityId, activityIdsInProgress,
                        studentsCountBySubjectId, approvedCountByActivityId);

        return PaginationHelper.fromPage(pageResult, dtos);
    }

    private List<Activity> loadActivitiesPreservingOrder(List<Long> orderedIds) {
        Map<Long, Activity> activitiesById = activityRepository.findAllByIdInWithSubject(orderedIds).stream()
                .collect(Collectors.toMap(Activity::getId, Function.identity()));

        return orderedIds.stream()
                .map(activitiesById::get)
                .filter(activity -> activity != null)
                .toList();
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

    private Map<Long, Integer> loadStudentsCountBySubjectId(List<Activity> activities) {
        List<Long> subjectIds = activities.stream()
                .map(activity -> activity.getSubject().getId())
                .distinct()
                .toList();

        if (subjectIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return subjectRepository.countStudentsGroupedBySubjectId(subjectIds).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).intValue()));
    }

    private Map<Long, Integer> loadApprovedCountByActivityId(List<Long> activityIds) {
        if (activityIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return activityCompletedRepository
                .countApprovedGroupedByActivityId(activityIds, ActivityCompletedState.APPROVED.ordinal())
                .stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).intValue()));
    }
}
