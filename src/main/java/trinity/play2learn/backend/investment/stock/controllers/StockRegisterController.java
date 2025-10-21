package trinity.play2learn.backend.investment.stock.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockRegisterService;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/investment/stocks")
@RestController
@AllArgsConstructor
public class StockRegisterController {

    private final IStockRegisterService stockRegisterService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<StockResponseDto>> register(
        @Valid @RequestBody StockRegisterRequestDto stockDto
    ) {
        return ResponseFactory.created(
            stockRegisterService.cu77registerStock(stockDto),
            SuccessfulMessages.createdSuccessfully("Accion")
        );
    }
    
}
