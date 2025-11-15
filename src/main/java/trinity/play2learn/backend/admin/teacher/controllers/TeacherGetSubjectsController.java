package trinity.play2learn.backend.admin.teacher.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherSubjectsDto;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetSubjectsService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/teacher/subjects")
public class TeacherGetSubjectsController {
    
    private final ITeacherGetSubjectsService teacherGetService;

    @GetMapping
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<TeacherSubjectsDto>> getSubjectsCoursesAndYears(@SessionUser User user) {
        
        return ResponseFactory.ok(teacherGetService.getSubjectsCoursesAndYears(user), SuccessfulMessages.okSuccessfully());
    }
}
