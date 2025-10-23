package trinity.play2learn.backend.investment.stock.controllers;

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
import trinity.play2learn.backend.investment.stock.dtos.request.StockBuyRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockBuyService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/stocks")
@RestController
@AllArgsConstructor
public class StockBuyController {

    private final IStockBuyService stockBuyService;

    @PostMapping ("/buy")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<StockBuyResponseDto>> buy(
        @Valid @RequestBody StockBuyRequestDto stockDto,
        @SessionUser User user
    ) {
        return ResponseFactory.created(
            stockBuyService.cu84buystocks(stockDto, user),
            SuccessfulMessages.createdSuccessfully("Accion")
        );
    }
    
}
