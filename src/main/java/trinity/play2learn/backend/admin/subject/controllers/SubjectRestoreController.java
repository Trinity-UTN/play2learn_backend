package trinity.play2learn.backend.admin.subject.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRestoreService;
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/subjects")
public class SubjectRestoreController {
    
    private final ISubjectRestoreService subjectRestoreService;

    @PatchMapping("/restore/{id}")
    public ResponseEntity<BaseResponse<SubjectResponseDto>> restore(@PathVariable Long id) {
        return ResponseFactory.ok(
            subjectRestoreService.cu34RestoreSubject(id), 
            SuccesfullyMessages.restoredSuccessfully("Materia")
        );
    }
}
