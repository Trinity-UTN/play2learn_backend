package trinity.play2learn.backend.investment.stock.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;

import java.math.BigInteger;

@ExtendWith(MockitoExtension.class)
class StockRegisterServiceTest {

    @Mock
    private IStockRepository stockRepository;

    @Mock
    private IStockHistoryRepository stockHistoryRepository;

    private StockRegisterService stockRegisterService;

    @BeforeEach
    void setUp() {
        stockRegisterService = new StockRegisterService(stockRepository, stockHistoryRepository);
    }

    @Nested
    @DisplayName("cu77registerStock")
    class Cu77registerStock {

        @Test
        @DisplayName("Given valid request When registering stock Then creates stock and initial history")
        void whenValidRequest_createsStockAndInitialHistory() {
            // Given - Request válido
            StockRegisterRequestDto requestDto = StockRegisterRequestDto.builder()
                    .name(InvestmentTestMother.DEFAULT_STOCK_NAME)
                    .abbreviation(InvestmentTestMother.DEFAULT_STOCK_ABBREVIATION)
                    .initialPrice(InvestmentTestMother.DEFAULT_INITIAL_PRICE)
                    .totalAmount(InvestmentTestMother.DEFAULT_TOTAL_AMOUNT)
                    .riskLevel(RiskLevel.MEDIO)
                    .build();
            Stock savedStock = InvestmentTestMother.defaultStock();
            StockHistory initialHistory = InvestmentTestMother.defaultStockHistory();

            when(stockRepository.save(any(Stock.class))).thenReturn(savedStock);
            when(stockHistoryRepository.save(any(StockHistory.class))).thenReturn(initialHistory);

            // When - Registrar acción
            try (
                    MockedStatic<StockMapper> stockMapperMock = mockStatic(StockMapper.class);
                    MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                stockMapperMock.when(() -> StockMapper.toModel(requestDto)).thenReturn(savedStock);
                historyMapperMock.when(() -> StockHistoryMapper.toModel(savedStock, 0.0)).thenReturn(initialHistory);
                stockMapperMock.when(() -> StockMapper.toDto(savedStock, null, null))
                        .thenReturn(InvestmentTestMother.stockResponseDto(savedStock.getId(),
                                savedStock.getName(), savedStock.getAbbreviation(), savedStock.getCurrentPrice(),
                                BigInteger.ZERO, null));

                StockResponseDto result = stockRegisterService.cu77registerStock(requestDto);

                // Then - Debe crear stock e historial inicial
                stockMapperMock.verify(() -> StockMapper.toModel(requestDto));
                verify(stockRepository).save(savedStock);
                historyMapperMock.verify(() -> StockHistoryMapper.toModel(savedStock, 0.0));
                verify(stockHistoryRepository).save(initialHistory);
                stockMapperMock.verify(() -> StockMapper.toDto(savedStock, null, null));
                assertThat(result).isNotNull();
            }
        }
    }
}

