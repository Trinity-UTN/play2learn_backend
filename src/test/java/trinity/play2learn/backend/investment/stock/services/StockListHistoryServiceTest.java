package trinity.play2learn.backend.investment.stock.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import trinity.play2learn.backend.investment.stock.dtos.response.StockHistoryResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockService;

@ExtendWith(MockitoExtension.class)
class StockListHistoryServiceTest {

    @Mock
    private IStockFindByIdService stockFindByIdService;

    @Mock
    private IStockHistoryFindByStockService stockHistoryFindByStockService;

    private StockListHistoryService stockListHistoryService;

    @BeforeEach
    void setUp() {
        stockListHistoryService = new StockListHistoryService(
            stockFindByIdService,
            stockHistoryFindByStockService
        );
    }

    @Nested
    @DisplayName("cu79getStockHistories")
    class Cu79getStockHistories {

        @Test
        @DisplayName("Given valid stock id When getting history Then returns list of history ordered by date")
        void whenValidStockId_returnsHistoryList() {
            // Given - ID válido
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory history1 = InvestmentTestMother.defaultStockHistory();
            StockHistory history2 = InvestmentTestMother.stockHistoryWithVariation(1202L, 0.05,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0);
            List<StockHistory> histories = Arrays.asList(history1, history2);

            StockHistoryResponseDto dto1 = InvestmentTestMother.stockHistoryResponseDto(history1.getId(),
                    history1.getPrice(), history1.getVariation(), history1.getCreatedAt());
            StockHistoryResponseDto dto2 = InvestmentTestMother.stockHistoryResponseDto(history2.getId(),
                    history2.getPrice(), history2.getVariation(), history2.getCreatedAt());
            List<StockHistoryResponseDto> expectedDtos = Arrays.asList(dto1, dto2);

            when(stockFindByIdService.execute(stock.getId())).thenReturn(stock);
            when(stockHistoryFindByStockService.execute(stock)).thenReturn(histories);

            // When - Obtener historial
            try (MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                historyMapperMock.when(() -> StockHistoryMapper.toDtoList(histories)).thenReturn(expectedDtos);

                List<StockHistoryResponseDto> result = stockListHistoryService.cu79getStockHistories(stock.getId());

                // Then - Debe retornar historial ordenado
                verify(stockFindByIdService).execute(stock.getId());
                verify(stockHistoryFindByStockService).execute(stock);
                historyMapperMock.verify(() -> StockHistoryMapper.toDtoList(histories));
                assertThat(result).hasSize(2);
                assertThat(result).isEqualTo(expectedDtos);
            }
        }

        @Test
        @DisplayName("Given stock with no history When getting history Then returns empty list")
        void whenNoHistory_returnsEmptyList() {
            // Given - Acción sin historial
            Stock stock = InvestmentTestMother.defaultStock();

            when(stockFindByIdService.execute(stock.getId())).thenReturn(stock);
            when(stockHistoryFindByStockService.execute(stock)).thenReturn(List.of());

            // When - Obtener historial
            try (MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                historyMapperMock.when(() -> StockHistoryMapper.toDtoList(List.of())).thenReturn(List.of());

                List<StockHistoryResponseDto> result = stockListHistoryService.cu79getStockHistories(stock.getId());

                // Then - Debe retornar lista vacía
                assertThat(result).isEmpty();
            }
        }
    }
}

