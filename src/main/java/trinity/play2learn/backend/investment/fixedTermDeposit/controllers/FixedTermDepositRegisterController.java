package trinity.play2learn.backend.investment.fixedTermDeposit.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositRegisterService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/fixed-term-deposit")
@RestController
@AllArgsConstructor
public class FixedTermDepositRegisterController {
    
    private final IFixedTermDepositRegisterService fixedTermDepositRegisterService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<FixedTermDepositResponseDto>> investFixedTermDeposit(
        @Valid @RequestBody FixedTermDepositRegisterRequestDto dto,
        @SessionUser User user
    ) {
        return ResponseFactory.created(
            fixedTermDepositRegisterService.cu92registerFixedTermDeposit(dto, user),
            SuccessfulMessages.createdSuccessfully("Plazo fijo")
        );
    }
}
