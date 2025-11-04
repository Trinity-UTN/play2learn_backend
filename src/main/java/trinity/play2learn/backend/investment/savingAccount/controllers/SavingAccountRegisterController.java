package trinity.play2learn.backend.investment.savingAccount.controllers;

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
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountRegisterRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountRegisterService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/saving-account")
@RestController
@AllArgsConstructor
public class SavingAccountRegisterController {

    private final ISavingAccountRegisterService savingAccountRegisterService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<SavingAccountResponseDto>> register(
        @Valid @RequestBody SavingAccountRegisterRequestDto dto,
        @SessionUser User user
    ) {
        return ResponseFactory.created(
            savingAccountRegisterService.cu102registerSavingAccount(dto, user),
            SuccessfulMessages.createdSuccessfully("Caja de ahorro")
        );
    }
    
}
