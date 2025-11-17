package trinity.play2learn.backend.benefits.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListPurchasesService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@RequestMapping("/benefits/teacher")
@AllArgsConstructor
public class BenefitListPurchasesController {
    
    private final IBenefitListPurchasesService benefitGetPurchasesService;

    @GetMapping("/purchases/{id}")
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<List<BenefitPurchaseSimpleResponseDto>>> listPurchases(@SessionUser User user, @PathVariable Long id) {
        return ResponseFactory.ok(benefitGetPurchasesService.cu98ListPurchasesByBenefitId(user, id), SuccessfulMessages.okSuccessfully());
    }
}
