package trinity.play2learn.backend.activity.activity.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetNotApprovedByStudentService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/activity/student")
public class ActivityGetByStudentController {
    
    private final IActivityGetNotApprovedByStudentService activityGetNotApprovedByStudentService;

    @GetMapping("/not-approved")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<List<ActivityStudentNotApprovedResponseDto>>> listNotApprovedActivities(@SessionUser User user) {

        return ResponseFactory.ok(activityGetNotApprovedByStudentService.cu62ListNotApprovedActivitiesByStudent(user), SuccessfulMessages.okSuccessfully());
    }
}
