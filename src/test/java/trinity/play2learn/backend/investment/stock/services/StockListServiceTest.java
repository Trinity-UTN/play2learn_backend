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
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;

@ExtendWith(MockitoExtension.class)
class StockListServiceTest {

    @Mock
    private IStockRepository stockRepository;

    private StockListService stockListService;

    @BeforeEach
    void setUp() {
        stockListService = new StockListService(stockRepository);
    }

    @Nested
    @DisplayName("cu86listStocks")
    class Cu86listStocks {

        @Test
        @DisplayName("Given stocks exist When listing Then returns all stocks")
        void whenStocksExist_returnsAllStocks() {
            // Given - Acciones existentes
            Stock stock1 = InvestmentTestMother.defaultStock();
            Stock stock2 = InvestmentTestMother.stock(1002L, "Otra Acción", "OA", InvestmentTestMother.DEFAULT_INITIAL_PRICE,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE, InvestmentTestMother.DEFAULT_TOTAL_AMOUNT,
                    InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT, InvestmentTestMother.DEFAULT_SOLD_AMOUNT,
                    trinity.play2learn.backend.investment.stock.models.RiskLevel.ALTO);
            List<Stock> stocks = Arrays.asList(stock1, stock2);

            StockResponseDto dto1 = InvestmentTestMother.stockResponseDto(stock1.getId(), stock1.getName(),
                    stock1.getAbbreviation(), stock1.getCurrentPrice(), null, null);
            StockResponseDto dto2 = InvestmentTestMother.stockResponseDto(stock2.getId(), stock2.getName(),
                    stock2.getAbbreviation(), stock2.getCurrentPrice(), null, null);
            List<StockResponseDto> expectedDtos = Arrays.asList(dto1, dto2);

            when(stockRepository.findAll()).thenReturn(stocks);

            // When - Listar acciones
            try (MockedStatic<StockMapper> stockMapperMock = mockStatic(StockMapper.class)) {
                stockMapperMock.when(() -> StockMapper.toDtoList(stocks)).thenReturn(expectedDtos);

                List<StockResponseDto> result = stockListService.cu86listStocks();

                // Then - Debe retornar todas las acciones
                verify(stockRepository).findAll();
                stockMapperMock.verify(() -> StockMapper.toDtoList(stocks));
                assertThat(result).hasSize(2);
                assertThat(result).isEqualTo(expectedDtos);
            }
        }

        @Test
        @DisplayName("Given no stocks exist When listing Then returns empty list")
        void whenNoStocksExist_returnsEmptyList() {
            // Given - Sin acciones
            when(stockRepository.findAll()).thenReturn(List.of());

            // When - Listar acciones
            try (MockedStatic<StockMapper> stockMapperMock = mockStatic(StockMapper.class)) {
                stockMapperMock.when(() -> StockMapper.toDtoList(List.of())).thenReturn(List.of());

                List<StockResponseDto> result = stockListService.cu86listStocks();

                // Then - Debe retornar lista vacía
                verify(stockRepository).findAll();
                stockMapperMock.verify(() -> StockMapper.toDtoList(List.of()));
                assertThat(result).isEmpty();
            }
        }
    }
}

