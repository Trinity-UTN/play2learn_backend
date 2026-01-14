package trinity.play2learn.backend.economy.wallet.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import org.springframework.http.ResponseEntity;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletRestartService;

@RestController
@RequestMapping("/wallet")
@AllArgsConstructor
public class WalletRestartController {

    private final IWalletRestartService walletRestartService;

    @PostMapping("/restart")
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<Void>> restart() {
        walletRestartService.execute();
        return ResponseFactory.ok(null, SuccessfulMessages.okSuccessfully());
    }
    
}
