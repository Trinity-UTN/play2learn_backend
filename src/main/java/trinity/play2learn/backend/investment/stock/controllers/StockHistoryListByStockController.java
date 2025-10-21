package trinity.play2learn.backend.investment.stock.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.investment.stock.dtos.response.StockHistoryResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListHistoryService;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/investment/stocks/histories")
@RestController
@AllArgsConstructor
public class StockHistoryListByStockController {

    private final IStockListHistoryService stockListHistoryService;

    @GetMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<List<StockHistoryResponseDto>>> get(@PathVariable Long id) {
        return ResponseFactory.ok (
            stockListHistoryService.cu79getStockHistories(id), 
            SuccessfulMessages.okSuccessfully()
        );
    }

}
