package trinity.play2learn.backend.admin.subject.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRefillBalanceService;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@AllArgsConstructor
@RequestMapping("/admin/subjects")
public class SubjectRefillBalanceController {

    private final ISubjectRefillBalanceService subjectRefillBalanceService;

    @PostMapping("/refill-balance")
    public ResponseEntity<BaseResponse<List<SubjectResponseDto>>> refill() {
        return ResponseFactory.ok(
            subjectRefillBalanceService.cu58RefillBalance(), 
            SuccessfulMessages.SUBJECT_ASSIGN_TEACHER_SUCCESFULLY
        );
    }
    
}
