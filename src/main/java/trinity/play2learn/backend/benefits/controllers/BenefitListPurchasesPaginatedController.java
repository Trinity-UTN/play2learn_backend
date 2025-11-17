package trinity.play2learn.backend.benefits.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListPurchasesPaginatedService;
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
@RequestMapping("/benefits/teacher")
public class BenefitListPurchasesPaginatedController {
    
    private final IBenefitListPurchasesPaginatedService benefitListPurchasesPaginatedService;

    @GetMapping("/purchases/paginated/{id}")
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<PaginatedData<BenefitPurchaseSimpleResponseDto>>> listPurchasesPaginated(
            @SessionUser User user,
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(name = "order_type", defaultValue = "asc") String orderType,
            @RequestParam(required = false) String search,
            @RequestParam(name = "filters", required = false) List<String> filters,
            @RequestParam(name = "filtersValues", required = false) List<String> filtersValues
    ) {
        return ResponseFactory.paginated(
            benefitListPurchasesPaginatedService.cu101ListPurchasesPaginated(user, id , page, pageSize, orderBy, orderType, search, filters, filtersValues), 
            SuccessfulMessages.okSuccessfully()
        );
    }
}
