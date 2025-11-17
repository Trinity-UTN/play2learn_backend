package trinity.play2learn.backend.economy.wallet.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletCompleteResponseDto;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletGetController {

    private final IWalletGetService walletGetService;

    @GetMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<WalletCompleteResponseDto>> getWallet(
        @SessionUser User user){ 
        
        return ResponseFactory.ok(
            walletGetService.cu70GetWallet(user), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
