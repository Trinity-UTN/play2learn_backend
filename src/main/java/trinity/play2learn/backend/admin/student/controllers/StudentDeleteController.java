package trinity.play2learn.backend.admin.student.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentDeleteService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/students")
@RestController
@AllArgsConstructor
public class StudentDeleteController {

    private final IStudentDeleteService studentDeleteService;

    @DeleteMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        studentDeleteService.cu19DeleteStudent(id);
        return ResponseFactory.noContent(
            SuccessfulMessages.deletedSuccessfully("Estudiante")
        );
    }
}
