package trinity.play2learn.backend.admin.subject.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRegisterService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/subjects")
public class SubjectRegisterController {
    
    private final ISubjectRegisterService subjectRegisterService;

    @PostMapping
    @SessionRequired(role = Role.ROLE_ADMIN)
    public ResponseEntity<BaseResponse<SubjectResponseDto>> create(@Valid @RequestBody SubjectRequestDto subjectDto) {
        return ResponseFactory.created(subjectRegisterService.cu28RegisterSubject(subjectDto), "Created succesfully");
    }

}
