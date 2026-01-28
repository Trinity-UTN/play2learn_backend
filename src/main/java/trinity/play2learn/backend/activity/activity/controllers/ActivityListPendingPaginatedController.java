package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentStateResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityListPendingPaginatedService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/activity/student/pending")
@AllArgsConstructor
public class ActivityListPendingPaginatedController {
    
    private final IActivityListPendingPaginatedService activityListPendingPaginatedService;

    @SessionRequired(roles = { Role.ROLE_STUDENT })
    @GetMapping("/paginated")
    public ResponseEntity<BaseResponse<PaginatedData<ActivityStudentStateResponseDto>>> listPendingByStudentPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(name = "order_type", defaultValue = "asc") String orderType,
            @RequestParam(required = false) String search,
            @RequestParam(name = "filters", required = false) List<String> filters,
            @RequestParam(name = "filtersValues", required = false) List<String> filtersValues,
            @SessionUser User user) {

        return ResponseFactory.paginated(
                activityListPendingPaginatedService.cu131ListPendingActivitiesByStudentPaginated(page, pageSize, orderBy,
                        orderType, search, filters, filtersValues, user),
                SuccessfulMessages.okSuccessfully());
    }
    
}
