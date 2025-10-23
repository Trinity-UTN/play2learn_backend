package trinity.play2learn.backend.benefits.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentCountResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitStudentCountService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/benefits/student")
public class BenefitStudentCountController {
    
    private final IBenefitStudentCountService benefitStudentCountService;
    
    @GetMapping("/count")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<BenefitStudentCountResponseDto>> count(@SessionUser User user) {
        return ResponseFactory.ok(benefitStudentCountService.cu89CountByStudentState(user), SuccessfulMessages.okSuccessfully());
    }

}
