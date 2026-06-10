package trinity.play2learn.backend.activity.activity.services.student;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentStateResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.ActivityApprovedNativeRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityApprovedListPaginatedService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityApprovedListPaginatedService implements IActivityApprovedListPaginatedService {

    private final IStudentGetByEmailService studentGetByEmailService;
    private final ActivityApprovedNativeRepository activityApprovedNativeRepository;
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<ActivityStudentStateResponseDto> cu69ListApprovedActivitiesPaginated(int page, int size,
            String orderBy, String orderType, String search, List<String> filters, List<String> filterValues,
            User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);

        Page<Long> completionIdPage = activityApprovedNativeRepository.findApprovedCompletionIds(
                student, pageable, search, filters, filterValues);

        if (completionIdPage.isEmpty()) {
            return PaginationHelper.fromPage(Page.empty(pageable), List.of());
        }

        List<ActivityCompleted> completions = loadCompletionsPreservingOrder(completionIdPage.getContent());
        Page<ActivityCompleted> pageResult = new PageImpl<>(completions, pageable, completionIdPage.getTotalElements());

        List<ActivityStudentStateResponseDto> dtos = completions.stream()
                .map(activityCompleted -> ActivityMapper.toStudentStateDto(
                        activityCompleted.getActivity(),
                        activityCompleted.getRemainingAttempts(),
                        activityCompleted.getReward(),
                        activityCompleted.getCompletedAt(),
                        activityCompleted.getState()))
                .toList();

        return PaginationHelper.fromPage(pageResult, dtos);
    }

    private List<ActivityCompleted> loadCompletionsPreservingOrder(List<Long> orderedIds) {
        Map<Long, ActivityCompleted> completionsById = activityCompletedRepository
                .findAllByIdInWithActivityAndSubject(orderedIds).stream()
                .collect(Collectors.toMap(ActivityCompleted::getId, Function.identity()));

        return orderedIds.stream()
                .map(completionsById::get)
                .filter(completion -> completion != null)
                .toList();
    }
}
