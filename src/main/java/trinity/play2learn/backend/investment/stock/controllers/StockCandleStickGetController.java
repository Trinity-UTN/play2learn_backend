package trinity.play2learn.backend.investment.stock.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.investment.stock.dtos.response.CandleStickChartValueResponseDto;
import trinity.play2learn.backend.investment.stock.models.RangeValue;
import trinity.play2learn.backend.investment.stock.services.interfaces.ICandleStickGetValuesService;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/investment/stocks/candlestick")
@RestController
@AllArgsConstructor
public class StockCandleStickGetController {

    private final ICandleStickGetValuesService candleStickGetValuesService;

    @GetMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT, Role.ROLE_TEACHER, Role.ROLE_ADMIN, Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<List<CandleStickChartValueResponseDto>>> get(
        @RequestParam(defaultValue = "0") Long stockId,
        @RequestParam(defaultValue = "DIARIO") RangeValue rangeValue
        ) {
        return ResponseFactory.ok (
            candleStickGetValuesService.cu83GetValuesCandleStick(stockId, rangeValue), 
            SuccessfulMessages.okSuccessfully()
        );
    }
    
}
