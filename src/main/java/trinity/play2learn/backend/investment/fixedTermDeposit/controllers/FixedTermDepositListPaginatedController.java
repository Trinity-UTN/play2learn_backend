package trinity.play2learn.backend.investment.fixedTermDeposit.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositListPaginatedService;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/fixed-term-deposit")
@RestController
@AllArgsConstructor
public class FixedTermDepositListPaginatedController {

    private final IFixedTermDepositListPaginatedService fixedTermDepositListPaginatedService;

    @GetMapping("/paginated")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<PaginatedData<FixedTermDepositResponseDto>>> listPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(name = "order_type", defaultValue = "asc") String orderType,
            @RequestParam(required = false) String search,
            @RequestParam(name = "filters", required = false) List<String> filters,
            @RequestParam(name = "filtersValues", required = false) List<String> filtersValues,
            @SessionUser User user
    ) {
        return ResponseFactory.paginated(
            fixedTermDepositListPaginatedService.cu99ListPaginatedFixedTermDeposits(
                page, pageSize, orderBy, orderType, search, filters, filtersValues, user),
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
