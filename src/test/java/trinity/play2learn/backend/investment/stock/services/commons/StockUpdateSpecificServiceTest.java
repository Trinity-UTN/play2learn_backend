package trinity.play2learn.backend.investment.stock.services.commons;

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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IOrderStopExecuteService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateVariationService;

@ExtendWith(MockitoExtension.class)
class StockUpdateSpecificServiceTest {

    @Mock
    private IStockRepository stockRepository;

    @Mock
    private IStockHistoryRepository stockHistoryRepository;

    @Mock
    private IStockCalculateVariationService stockCalculateVariationService;

    @Mock
    private IOrderStopExecuteService orderStopExecuteService;

    private StockUpdateSpecificService stockUpdateSpecificService;

    @BeforeEach
    void setUp() {
        stockUpdateSpecificService = new StockUpdateSpecificService(
            stockRepository,
            stockHistoryRepository,
            stockCalculateVariationService,
            orderStopExecuteService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stock with variation When updating specific Then updates price within limits and saves history")
        void whenStockWithVariation_updatesPriceAndSavesHistory() {
            // Given - Acción con variación
            Stock stock = InvestmentTestMother.defaultStock();
            Double variation = 5.0;
            Double currentPrice = stock.getCurrentPrice();
            Double expectedNewPrice = Math.min(Math.max(currentPrice * (1 + variation / 100), 10.0),
                    stock.getInitialPrice().doubleValue() * 2.5);
            Stock savedStock = InvestmentTestMother.defaultStock();
            savedStock.setCurrentPrice(expectedNewPrice);
            StockHistory history = InvestmentTestMother.defaultStockHistory();

            when(stockCalculateVariationService.execute(stock)).thenReturn(variation);
            when(stockRepository.save(any(Stock.class))).thenReturn(savedStock);
            when(stockHistoryRepository.save(any(StockHistory.class))).thenReturn(history);

            // When - Actualizar acción específica
            try (MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                historyMapperMock.when(() -> StockHistoryMapper.toModel(savedStock, variation)).thenReturn(history);

                stockUpdateSpecificService.execute(stock);

                // Then - Debe actualizar precio y guardar historial
                ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
                verify(stockRepository).save(stockCaptor.capture());

                Stock capturedStock = stockCaptor.getValue();
                assertThat(capturedStock.getCurrentPrice()).isEqualTo(expectedNewPrice);
                assertThat(capturedStock.getCurrentPrice()).isGreaterThanOrEqualTo(10.0);
                assertThat(capturedStock.getCurrentPrice())
                        .isLessThanOrEqualTo(stock.getInitialPrice().doubleValue() * 2.5);

                historyMapperMock.verify(() -> StockHistoryMapper.toModel(savedStock, variation));
                verify(stockHistoryRepository).save(history);
                verify(orderStopExecuteService).execute(savedStock);
            }
        }

        @Test
        @DisplayName("Given stock with variation exceeding upper limit When updating specific Then sets price to upper limit")
        void whenVariationExceedsUpperLimit_setsPriceToUpperLimit() {
            // Given - Acción con variación que excede límite superior
            Stock stock = InvestmentTestMother.defaultStock();
            Double variation = 200.0; // Variación muy alta
            Double upperLimit = stock.getInitialPrice().doubleValue() * 2.5;

            when(stockCalculateVariationService.execute(stock)).thenReturn(variation);
            when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When - Actualizar acción específica
            try (MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                historyMapperMock.when(() -> StockHistoryMapper.toModel(any(Stock.class), any(Double.class)))
                        .thenReturn(InvestmentTestMother.defaultStockHistory());

                stockUpdateSpecificService.execute(stock);

                // Then - Debe establecer precio al límite superior
                ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
                verify(stockRepository).save(stockCaptor.capture());

                Stock capturedStock = stockCaptor.getValue();
                assertThat(capturedStock.getCurrentPrice()).isLessThanOrEqualTo(upperLimit);
            }
        }

        @Test
        @DisplayName("Given stock with variation below lower limit When updating specific Then sets price to lower limit")
        void whenVariationBelowLowerLimit_setsPriceToLowerLimit() {
            // Given - Acción con variación que está por debajo del límite inferior
            Stock stock = InvestmentTestMother.defaultStock();
            stock.setCurrentPrice(5.0); // Precio muy bajo
            Double variation = -50.0; // Variación negativa muy alta

            when(stockCalculateVariationService.execute(stock)).thenReturn(variation);
            when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When - Actualizar acción específica
            try (MockedStatic<StockHistoryMapper> historyMapperMock = mockStatic(StockHistoryMapper.class)) {
                historyMapperMock.when(() -> StockHistoryMapper.toModel(any(Stock.class), any(Double.class)))
                        .thenReturn(InvestmentTestMother.defaultStockHistory());

                stockUpdateSpecificService.execute(stock);

                // Then - Debe establecer precio al límite inferior (10.0)
                ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
                verify(stockRepository).save(stockCaptor.capture());

                Stock capturedStock = stockCaptor.getValue();
                assertThat(capturedStock.getCurrentPrice()).isGreaterThanOrEqualTo(10.0);
            }
        }
    }
}

