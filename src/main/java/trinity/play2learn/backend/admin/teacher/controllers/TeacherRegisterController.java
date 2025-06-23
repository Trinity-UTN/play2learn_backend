package trinity.play2learn.backend.admin.teacher.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRegisterService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@RequestMapping("/admin/teachers")
@AllArgsConstructor
public class TeacherRegisterController {
    
    private ITeacherRegisterService teacherRegisterService;

    @PostMapping
    @SessionRequired(role = Role.ROLE_ADMIN)
    public ResponseEntity<BaseResponse<TeacherResponseDto>> register(@Valid @RequestBody TeacherRequestDto teacherDto) {

        return ResponseFactory.created(teacherRegisterService.register(teacherDto),"Created succesfully");
    }
    
}
