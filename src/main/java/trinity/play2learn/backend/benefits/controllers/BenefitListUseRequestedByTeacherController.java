package trinity.play2learn.backend.benefits.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUseRequestedService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@AllArgsConstructor
@RequestMapping("/benefits/teacher")
public class BenefitListUseRequestedByTeacherController {
    
    private final IBenefitListUseRequestedService benefitListUseRequestedByTeacherService;

    @GetMapping("use-requested")
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<List<BenefitPurchaseSimpleResponseDto>>> listUseRequestedByTeacher(@SessionUser User user) {
        return ResponseFactory.ok(benefitListUseRequestedByTeacherService.cu82ListUseRequestedByTeacher(user), SuccessfulMessages.okSuccessfully());
    }
    
}
