package trinity.play2learn.backend.investment.stock.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.response.CandleStickChartValueResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.RangeValue;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockAndRangeService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockService;

@ExtendWith(MockitoExtension.class)
class CandleStickGetValuesServiceTest {

    @Mock
    private IStockFindByIdService stockFindByIdService;

    @Mock
    private IStockHistoryFindByStockService stockHistoryFindByStockService;

    @Mock
    private IStockHistoryFindByStockAndRangeService stockHistoryFindByStockAndRangeService;

    private CandleStickGetValuesService candleStickGetValuesService;

    @BeforeEach
    void setUp() {
        candleStickGetValuesService = new CandleStickGetValuesService(
            stockFindByIdService,
            stockHistoryFindByStockService,
            stockHistoryFindByStockAndRangeService
        );
    }

    @Nested
    @DisplayName("cu83GetValuesCandleStick")
    class Cu83GetValuesCandleStick {

        @Test
        @DisplayName("Given valid stock id and range value When getting candlestick values Then returns calculated values")
        void whenValidStockIdAndRange_returnsCalculatedValues() {
            // Given - ID válido y rango
            Stock stock = InvestmentTestMother.defaultStock();
            RangeValue rangeValue = RangeValue.SEMANAL;
            StockHistory history1 = InvestmentTestMother.defaultStockHistory();
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, 0.05,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0);
            List<StockHistory> histories = Arrays.asList(history1, history2);

            when(stockFindByIdService.execute(stock.getId())).thenReturn(stock);
            when(stockHistoryFindByStockAndRangeService.execute(eq(stock), any(LocalDateTime.class),
                    any(LocalDateTime.class))).thenReturn(histories);

            // When - Obtener valores candlestick
            try (MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                CandleStickChartValueResponseDto dto1 = InvestmentTestMother.candleStickChartValueResponseDto(
                        history1.getCreatedAt().toLocalDate(), history1.getPrice(), history1.getPrice(),
                        history1.getPrice() + 1.0, history1.getPrice() - 1.0);
                CandleStickChartValueResponseDto dto2 = InvestmentTestMother.candleStickChartValueResponseDto(
                        history2.getCreatedAt().toLocalDate(), history1.getPrice(), history2.getPrice(),
                        history2.getPrice() + 1.0, history2.getPrice() - 1.0);

                historyMapperMock.when(() -> StockHistoryMapper.toCandleStickValueDto(any(LocalDateTime.class),
                        any(Double.class), any(Double.class), any(Double.class), any(Double.class)))
                        .thenReturn(dto1, dto2);

                List<CandleStickChartValueResponseDto> result = candleStickGetValuesService
                        .cu83GetValuesCandleStick(stock.getId(), rangeValue);

                // Then - Debe retornar valores calculados
                verify(stockFindByIdService).execute(stock.getId());
                verify(stockHistoryFindByStockAndRangeService).execute(eq(stock), any(LocalDateTime.class),
                        any(LocalDateTime.class));
                assertThat(result).hasSize(2);
            }
        }

        @Test
        @DisplayName("Given HISTORICO range value When getting candlestick values Then returns all history")
        void whenHistoricoRange_returnsAllHistory() {
            // Given - Rango histórico
            Stock stock = InvestmentTestMother.defaultStock();
            RangeValue rangeValue = RangeValue.HISTORICO;
            StockHistory history1 = InvestmentTestMother.defaultStockHistory();
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, 0.05,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0);
            List<StockHistory> histories = Arrays.asList(history1, history2);

            when(stockFindByIdService.execute(stock.getId())).thenReturn(stock);
            when(stockHistoryFindByStockService.execute(stock)).thenReturn(histories);

            // When - Obtener valores candlestick histórico
            try (MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                CandleStickChartValueResponseDto dto1 = InvestmentTestMother.candleStickChartValueResponseDto(
                        history1.getCreatedAt().toLocalDate(), history1.getPrice(), history1.getPrice(),
                        history1.getPrice() + 1.0, history1.getPrice() - 1.0);
                CandleStickChartValueResponseDto dto2 = InvestmentTestMother.candleStickChartValueResponseDto(
                        history2.getCreatedAt().toLocalDate(), history1.getPrice(), history2.getPrice(),
                        history2.getPrice() + 1.0, history2.getPrice() - 1.0);

                historyMapperMock.when(() -> StockHistoryMapper.toCandleStickValueDto(any(LocalDateTime.class),
                        any(Double.class), any(Double.class), any(Double.class), any(Double.class)))
                        .thenReturn(dto1, dto2);

                List<CandleStickChartValueResponseDto> result = candleStickGetValuesService
                        .cu83GetValuesCandleStick(stock.getId(), rangeValue);

                // Then - Debe retornar todo el historial
                verify(stockHistoryFindByStockService).execute(stock);
                verify(stockHistoryFindByStockAndRangeService, org.mockito.Mockito.never()).execute(any(Stock.class),
                        any(LocalDateTime.class), any(LocalDateTime.class));
                assertThat(result).hasSize(2);
            }
        }
    }
}

