package trinity.play2learn.backend.admin.student.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentRestoreService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/students")
@RestController
@AllArgsConstructor
public class StudentRestoreController {
    
    private final IStudentRestoreService studentRestoreService;

    @PatchMapping ("/restore/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<StudentResponseDto>> restore (@PathVariable Long id) {
        return ResponseFactory.ok (
            studentRestoreService.cu38RestoreStudent(id), 
            SuccesfullyMessages.restoredSuccessfully("Estudiante")
        );
    }
}
