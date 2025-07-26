package trinity.play2learn.backend.admin.teacher.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/teachers")
public class TeacherGetController {
    
    private final ITeacherGetService teacherGetService;

    @GetMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<TeacherResponseDto>> getTeacherById(@PathVariable Long id) {
        
        return ResponseFactory.ok(teacherGetService.cu28GetTeacherById(id), "Ok");
    }
}
