package trinity.play2learn.backend.benefits.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitPurchaseService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RestController
@AllArgsConstructor
@RequestMapping("/benefits/purchase")
public class BenefitPurchaseController {
    
    private final IBenefitPurchaseService benefitPurchaseService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<BenefitPurchaseResponseDto>> purchase(@SessionUser User user, @Valid @RequestBody BenefitPurchaseRequestDto benefitRequestDto) {
        return ResponseFactory.created(benefitPurchaseService.cu75PurchaseBenefit(benefitRequestDto, user), SuccessfulMessages.createdSuccessfully("Compra de beneficio")); 
    }
}
