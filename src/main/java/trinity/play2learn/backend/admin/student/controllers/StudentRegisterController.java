package trinity.play2learn.backend.admin.student.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.services.StudentRegisterService;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

@RequestMapping("/admin/students")
@RestController
@AllArgsConstructor
public class StudentRegisterController {
    
    private final StudentRegisterService studentRegisterService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentResponseDto>> create(@Valid @RequestBody StudentRequestDto studentDto) {
        return ResponseFactory.created(studentRegisterService.cu4registerStudent(studentDto), "Created succesfully");
    }
}
