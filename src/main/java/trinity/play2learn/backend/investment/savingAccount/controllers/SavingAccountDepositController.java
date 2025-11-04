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
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountDepositRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountDepositService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/saving-account")
@RestController
@AllArgsConstructor
public class SavingAccountDepositController {

    private final ISavingAccountDepositService savingAccountDepositService;

    @PostMapping ("/deposit")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<SavingAccountResponseDto>> deposit(
        @Valid @RequestBody SavingAccountDepositRequestDto dto,
        @SessionUser User user
    ) {
        return ResponseFactory.created(
            savingAccountDepositService.cu103depositSavingAccount(dto, user),
            SuccessfulMessages.createdSuccessfully("Deposito en caja de ahorro")
        );
    }
    
}
