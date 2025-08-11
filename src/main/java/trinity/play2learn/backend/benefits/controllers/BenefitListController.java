package trinity.play2learn.backend.benefits.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.BenefitResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByTeacherService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/benefits")
public class BenefitListController {
    
    private final IBenefitListByTeacherService benefitListService;

    @GetMapping("/teacher")
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<List<BenefitResponseDto>>> listByTeacher(@SessionUser User user) {
        
        return ResponseFactory.ok(benefitListService.cu55ListBenefitsByTeacher(user), SuccessfulMessages.okSuccessfully());
    }
}
