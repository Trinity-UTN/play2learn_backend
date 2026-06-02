package trinity.play2learn.backend.activity.activity.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.noLudica.NoLudicaAttemptResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetNoLudicaAttemptService;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/activity/teacher/no-ludica")
public class ActivityGetNoLudicaAttemptController {

    private final IActivityGetNoLudicaAttemptService activityGetNoLudicaAttemptService;

    //Endpoint que devuelve el texto plano de la respuesta del estudiante y la metadata sobre el archivo.
    @GetMapping("/{activityCompletedId}")
    public ResponseEntity<BaseResponse<NoLudicaAttemptResponseDto>> getNoLudicaAttempt(@PathVariable Long activityCompletedId, @SessionUser User user) {
        return ResponseFactory.ok(activityGetNoLudicaAttemptService.cu127GetNoLudicaAttempt(activityCompletedId, user),SuccessfulMessages.okSuccessfully());
    }
}
