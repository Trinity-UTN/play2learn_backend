package trinity.play2learn.backend.admin.student.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.dtos.StudentUpdateRequestDto;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentUpdateService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;


@RequestMapping("/admin/students")
@RestController
@AllArgsConstructor
public class StudentUpdateController {

    private final IStudentUpdateService studentUpdateService;

    @PutMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<StudentResponseDto>> update(@Valid @RequestBody StudentUpdateRequestDto studentDto) {
        return ResponseFactory.created(studentUpdateService.cu18updateStudent(studentDto), "Updated succesfully");
    }
    
}
