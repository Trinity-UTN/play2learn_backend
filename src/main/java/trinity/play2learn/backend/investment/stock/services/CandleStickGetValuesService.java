package trinity.play2learn.backend.investment.stock.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.dtos.response.CandleStickChartValueResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.RangeValue;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.services.interfaces.ICandleStickGetValuesService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockAndRangeService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockService;

@Service
@AllArgsConstructor
public class CandleStickGetValuesService implements ICandleStickGetValuesService{

    private final IStockFindByIdService stockFindByIdService;

    private final IStockHistoryFindByStockService stockHistoryFindByStockService;

    private final IStockHistoryFindByStockAndRangeService stockHistoryFindByStockAndRangeService;
    
    @Override
    public List<CandleStickChartValueResponseDto> cu83GetValuesCandleStick(Long stockId, RangeValue rangeValue) {
        
        Stock stock = stockFindByIdService.execute(stockId);
        
        LocalDateTime startRange = calculateStartRange(rangeValue);

        List<StockHistory> stockHistories;

        if (startRange == null) {
            stockHistories = stockHistoryFindByStockService.execute(stock);
        }else {
            stockHistories = stockHistoryFindByStockAndRangeService.execute(stock, startRange, LocalDateTime.now());
        }

        List<CandleStickChartValueResponseDto> candleStickValues = new ArrayList<>();

        Double lastClose = null;

        Random random = new Random();

        for (StockHistory stockHistory : stockHistories) {
            if (lastClose == null) {
                lastClose = stockHistory.getPrice();
            }
            candleStickValues.add(
                StockHistoryMapper.toCandleStickValueDto(
                    stockHistory.getCreatedAt(),
                    lastClose,
                    stockHistory.getPrice(),
                    Math.max(lastClose, stockHistory.getPrice()) + random.nextDouble(), // High: Lo calcula como el maximo entre el precio anterior y el actual + un random
                    Math.min(lastClose, stockHistory.getPrice()) - random.nextDouble() // Low: Lo calcula como el minimo entre el precio anterior y el actual - un random
                )
            );
            lastClose = stockHistory.getPrice();
        }

        return candleStickValues;
    }

    private LocalDateTime calculateStartRange (RangeValue rangeValue) {
        // Obtiene la fecha de inicio segun el rango seleccionado
        
        if (rangeValue == RangeValue.HISTORICO) {
            return null;
        };

        return LocalDateTime.now().minusDays(rangeValue.getValor());
    }
    
}
