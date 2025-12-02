package trinity.play2learn.backend.profile.ranking.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.ranking.models.LeaderboardType;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingByCoinsService;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.profile.ranking.mappers.LeaderboardRequestMapper;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.configs.response.BaseResponse;

@RestController
@RequestMapping("/ranking/coins")
@AllArgsConstructor
public class RankingByCoinsController {

    private final IRankingByCoinsService rankingByCoinsService;

    @GetMapping({"/{type}", "/{type}/{id}"})
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<LeaderboardResponseDto>> getRankingByCoins(
        @PathVariable LeaderboardType type,
        @PathVariable(required = false) Long id,
        @SessionUser User user
    ) {
        return ResponseFactory.ok(
            rankingByCoinsService.cu120rankingByCoins(LeaderboardRequestMapper.toDto(type, id), user),
            SuccessfulMessages.okSuccessfully()
        );
    }
}
