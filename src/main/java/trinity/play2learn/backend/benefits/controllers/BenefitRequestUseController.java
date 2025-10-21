package trinity.play2learn.backend.benefits.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitRequestUseService;
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
public class BenefitRequestUseController {
    
    private final IBenefitRequestUseService benefitRequestUseService;

    @PatchMapping("/request-use/{id}")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<BenefitPurchaseSimpleResponseDto>> requestUse(@SessionUser User user, @PathVariable Long id) {
    
        return ResponseFactory.ok(benefitRequestUseService.cu81RequestBenefitUse(user, id), SuccessfulMessages.updatedSuccessfully("Compra del beneficio"));
    }
}
