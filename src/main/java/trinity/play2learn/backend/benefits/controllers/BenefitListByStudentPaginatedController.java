package trinity.play2learn.backend.benefits.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByStudentPaginatedService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/benefits")
public class BenefitListByStudentPaginatedController {
    
    private final IBenefitListByStudentPaginatedService benefitListByStudentPaginatedService;

    @GetMapping("/student/paginated")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<PaginatedData<BenefitStudentResponseDto>>> listByStudentPaginated(
            @SessionUser User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(name = "order_type", defaultValue = "asc") String orderType,
            @RequestParam(required = false) String search,
            @RequestParam(name = "filters", required = false) List<String> filters,
            @RequestParam(name = "filtersValues", required = false) List<String> filtersValues
    ) {
        return ResponseFactory.paginated(
            benefitListByStudentPaginatedService.cu80ListBenefitsByStudentPaginated(user, page, pageSize, orderBy, orderType, search, filters, filtersValues), 
            SuccessfulMessages.okSuccessfully()
        );
    }
}
