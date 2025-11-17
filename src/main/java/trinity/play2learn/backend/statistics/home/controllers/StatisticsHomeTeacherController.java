package trinity.play2learn.backend.statistics.home.controllers;

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
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeTeacherResponseDto;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsHomeTeacherService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/statistics/home")
@RestController
@AllArgsConstructor
public class StatisticsHomeTeacherController {

    private final IStatisticsHomeTeacherService statisticsHomeTeacherService;
    
    @GetMapping ("/teacher")
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<StatisticsHomeTeacherResponseDto>> get(
        @SessionUser User user
    ) {
        return ResponseFactory.ok(
            statisticsHomeTeacherService.cu68GetStatisticsHomeTeacher(user), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
