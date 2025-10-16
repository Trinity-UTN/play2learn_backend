package trinity.play2learn.backend.benefits.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGenerateService;
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
public class BenefitGenerateController {
    
    private final IBenefitGenerateService benefitGenerateService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<BenefitResponseDto>> generate(@SessionUser User user, @Valid @RequestBody BenefitRequestDto benefitRequestDto) {
        return ResponseFactory.created(benefitGenerateService.cu51GenerateBenefit(benefitRequestDto, user), SuccessfulMessages.createdSuccessfully("Beneficio")); 
    }
}
