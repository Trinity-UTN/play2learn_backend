package trinity.play2learn.backend.admin.student.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetSubjectsService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import trinity.play2learn.backend.admin.subject.dtos.SubjectSimplifiedResponseDto;


@RestController
@AllArgsConstructor
@RequestMapping("/admin/students")
public class StudentGetSubjectsController {
    
    private final IStudentGetSubjectsService studentGetSubjectsService;

    @GetMapping("/subjects")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<List<SubjectSimplifiedResponseDto>>> getSubjectsByStudent(
        @SessionUser User user
    ) {
        return ResponseFactory.ok(
            studentGetSubjectsService.cu124GetSubjectsByStudent(
                user
            ), 
            SuccessfulMessages.okSuccessfully()
        );
    }
}
