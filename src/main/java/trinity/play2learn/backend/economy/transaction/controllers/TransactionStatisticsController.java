package trinity.play2learn.backend.economy.transaction.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionStatisticsResponseDto;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionListStatisticsService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class TransactionStatisticsController {

    private final ITransactionListStatisticsService transactionListStatisticsService;

    @GetMapping
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<List<TransactionStatisticsResponseDto>>> get(){ 
        
        return ResponseFactory.ok(
            transactionListStatisticsService.execute(), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
