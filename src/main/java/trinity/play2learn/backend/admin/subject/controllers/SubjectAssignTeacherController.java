package trinity.play2learn.backend.admin.subject.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAssignTeacherService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectUnassignTeacherService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/subjects")
public class SubjectAssignTeacherController {
    
    private final ISubjectAssignTeacherService subjectAssignTeacherService;
    private final ISubjectUnassignTeacherService subjectUnassignTeacherService;


    @PatchMapping("/assign-teacher/{subjectId}/{teacherId}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<SubjectResponseDto>> assignTeacher(@PathVariable Long subjectId,@PathVariable Long teacherId) {
        return ResponseFactory.ok( subjectAssignTeacherService.cu49AssignTeacher(subjectId, teacherId), SuccessfulMessages.SUBJECT_ASSIGN_TEACHER_SUCCESFULLY);
    }

    @PatchMapping("/unassign-teacher/{subjectId}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<SubjectResponseDto>> unassignTeacher(@PathVariable Long subjectId) {
        return ResponseFactory.ok( subjectUnassignTeacherService.cu50UnassignTeacher(subjectId), SuccessfulMessages.SUBJECT_UNASSIGN_TEACHER_SUCCESFULLY);
    }
}
