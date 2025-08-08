package trinity.play2learn.backend.admin.teacher.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherUpdateDto;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherUpdateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@AllArgsConstructor
@RequestMapping("/admin/teachers")
public class TeacherUpdateController {
    
    private final ITeacherUpdateService teacherUpdateService;

    @PutMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<TeacherResponseDto>> update(@PathVariable Long id, @Valid @RequestBody TeacherUpdateDto teacherDto) {
        
        return ResponseFactory.ok(teacherUpdateService.cu23UpdateTeacher(id, teacherDto), SuccessfulMessages.updatedSuccessfully("Docente"));
    }
}
