package trinity.play2learn.backend.admin.subject.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentSimplificatedResponse;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListAllStudentsService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/subjects")
public class SubjectListAllStudentsController {

    private final ISubjectListAllStudentsService subjectListAllStudentsService;

    @GetMapping("/all-students/{subjectId}")
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<List<StudentSimplificatedResponse>>> get(
        @PathVariable Long subjectId
    ) {
        
        return ResponseFactory.ok(
            subjectListAllStudentsService.cu101ListAllStudentsBySubject(subjectId), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
