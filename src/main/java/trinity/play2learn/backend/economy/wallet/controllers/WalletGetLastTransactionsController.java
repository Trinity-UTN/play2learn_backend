package trinity.play2learn.backend.economy.wallet.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionResponseDto;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetLastTransactionsService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletGetLastTransactionsController {

    private final IWalletGetLastTransactionsService walletGetLastTransactionsService;

    @GetMapping("/last-transactions")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<List<TransactionResponseDto>>> getLastTransactions(
        @SessionUser User user){ 
        
        return ResponseFactory.ok(
            walletGetLastTransactionsService.cu65GetLastTransactions(user), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
