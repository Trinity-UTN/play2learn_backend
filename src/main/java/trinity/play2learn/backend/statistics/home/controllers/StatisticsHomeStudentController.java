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
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeStudentResponseDto;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsHomeStudentService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/statistics/home")
@RestController
@AllArgsConstructor
public class StatisticsHomeStudentController {

    private final IStatisticsHomeStudentService statisticsHomeStudentService;

    @GetMapping ("/student")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<StatisticsHomeStudentResponseDto>> get(
        @SessionUser User user
    ) {
        return ResponseFactory.ok(
            statisticsHomeStudentService.cu73StatisticsHomeStudent(user), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
