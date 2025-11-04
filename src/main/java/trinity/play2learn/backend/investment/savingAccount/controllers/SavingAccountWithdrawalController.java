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
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountWithdrawalRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountWithdrawalService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/saving-account")
@RestController
@AllArgsConstructor
public class SavingAccountWithdrawalController {

    private final ISavingAccountWithdrawalService savingAccountWithdrawalService;

    @PostMapping ("/withdrawal")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<SavingAccountResponseDto>> withdrawal(
        @Valid @RequestBody SavingAccountWithdrawalRequestDto dto,
        @SessionUser User user
    ) {
        return ResponseFactory.created(
            savingAccountWithdrawalService.cu104withdrawalSavingAccount(dto, user),
            SuccessfulMessages.createdSuccessfully("Retiro de caja de ahorro")
        );
    }
    
}
