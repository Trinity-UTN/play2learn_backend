package trinity.play2learn.backend.investment.savingAccount.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountDeleteService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/saving-account")
@RestController
@AllArgsConstructor
public class SavingAccountDeleteController {

    private final ISavingAccountDeleteService savingAccountDeleteService;

    @DeleteMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<Void>> deleted(
        @PathVariable Long id,
        @SessionUser User user
    ) {
        savingAccountDeleteService.cu105deleteSavingAccount(id, user);
        return ResponseFactory.noContent(
            SuccessfulMessages.createdSuccessfully("Caja de ahorro")
        );
    }
    
}
