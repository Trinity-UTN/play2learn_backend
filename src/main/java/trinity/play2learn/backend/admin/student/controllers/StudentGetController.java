package trinity.play2learn.backend.admin.student.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RequestMapping("/admin/students")
@RestController
@AllArgsConstructor
public class StudentGetController {

    private final IStudentGetService studentGetService;
    
    @GetMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<StudentResponseDto>> get(@PathVariable Long id) {
        return ResponseFactory.ok (
            studentGetService.cu22GetStudent(id), 
            SuccesfullyMessages.okSuccessfully()
        );
    }
    
}
