package trinity.play2learn.backend.statistics.investment.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.statistics.investment.dtos.response.FixedTermDepositStatisticsResponseDto;
import trinity.play2learn.backend.statistics.investment.services.interfaces.IFixedTermDepositStatisticsService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/statistics/investment/fixed-term-deposit")
@RestController
@AllArgsConstructor
public class FixedTermDepositStatisticsController {

    private final IFixedTermDepositStatisticsService fixedTermDepositStatisticsService;

    @GetMapping 
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<FixedTermDepositStatisticsResponseDto>> get(
        @SessionUser User user
    ) {
        return ResponseFactory.ok(
            fixedTermDepositStatisticsService.execute(user), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
