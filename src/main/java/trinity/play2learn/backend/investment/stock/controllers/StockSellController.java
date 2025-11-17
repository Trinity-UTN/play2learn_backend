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
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockSellService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@RequestMapping("/investment/stocks")
@RestController
@AllArgsConstructor
public class StockSellController {

    private final IStockSellService stockSellService;

    @PostMapping ("/sell")
    @SessionRequired(roles = {Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<StockSellResponseDto>> sell(
        @Valid @RequestBody StockBuyRequestDto stockDto,
        @SessionUser User user
    ) {
        return ResponseFactory.created(
            stockSellService.cu90sellStock(stockDto, user),
            SuccessfulMessages.createdSuccessfully("Orden de venta de accion")
        );
    }
    
}
