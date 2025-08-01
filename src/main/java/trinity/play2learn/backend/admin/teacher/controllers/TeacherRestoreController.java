package trinity.play2learn.backend.admin.teacher.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRestoreService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/teachers")
public class TeacherRestoreController {
    
    private final ITeacherRestoreService teacherRestoreService;

    @PatchMapping("/restore/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<TeacherResponseDto>> restore(@PathVariable Long id) {

        return ResponseFactory.ok(teacherRestoreService.cu35RestoreTeacher(id), SuccessfulMessages.restoredSuccessfully("Docente"));
    }
}
