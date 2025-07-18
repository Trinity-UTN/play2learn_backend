package trinity.play2learn.backend.admin.subject.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectAddResponseDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRemoveStudentsService;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

@RestController
@AllArgsConstructor
@RequestMapping("admin/subjects")
public class SubjectRemoveStudentsController {
    
    private final ISubjectRemoveStudentsService subjectRemoveStudentsService;

    @PatchMapping("remove-students/{subjectId}")
    public ResponseEntity<BaseResponse<SubjectAddResponseDto>> remove(@PathVariable Long subjectId , @RequestBody List<Long> studentIds) {

        return ResponseFactory.ok(subjectRemoveStudentsService.cu37RemoveStudentsFromSubject(subjectId , studentIds), "Students removed from subject"); 
    }
}
