package trinity.play2learn.backend.investment.stock.controllers;

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
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListService;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/investment/stocks")
@RestController
@AllArgsConstructor
public class StockListController {

    private final IStockListService stockListService;

    @GetMapping
    @SessionRequired(roles = {Role.ROLE_DEV, Role.ROLE_ADMIN, Role.ROLE_STUDENT, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<List<StockResponseDto>>> get() {
        return ResponseFactory.ok (
            stockListService.cu86listStocks(), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
