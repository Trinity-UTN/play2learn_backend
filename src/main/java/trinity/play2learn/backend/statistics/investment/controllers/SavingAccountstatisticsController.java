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
import trinity.play2learn.backend.statistics.investment.dtos.response.SavingAccountStatisticsResponseDto;
import trinity.play2learn.backend.statistics.investment.services.interfaces.ISavingAccountStatisticsService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/statistics/investment/saving-account")
@RestController
@AllArgsConstructor
public class SavingAccountstatisticsController {

    private final ISavingAccountStatisticsService savingAccountStatisticsService;

    @GetMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<SavingAccountStatisticsResponseDto>> get(
        @SessionUser User user
    ) {
        return ResponseFactory.ok(
            savingAccountStatisticsService.execute(user), 
            SuccessfulMessages.okSuccessfully()
        );
    }
}
