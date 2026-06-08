package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryCalculateTrendService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindLastService;

@ExtendWith(MockitoExtension.class)
class StockCalculateVariationServiceTest {

    @Mock
    private IStockHistoryFindLastService stockHistoryFindLastService;

    @Mock
    private IStockHistoryCalculateTrendService stockHistoryCalculateTrendService;

    private StockCalculateVariationService stockCalculateVariationService;

    @BeforeEach
    void setUp() {
        stockCalculateVariationService = new StockCalculateVariationService(
            stockHistoryFindLastService,
            stockHistoryCalculateTrendService
        );
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given stock with bullish trend When calculating variation Then returns value within expected range")
        void whenBullishTrend_returnsValueWithinRange() {
            // Given - Acción con tendencia alcista
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();
            lastHistory.setSoldAmount(BigInteger.valueOf(100));
            stock.setSoldAmount(BigInteger.valueOf(150)); // Incremento de ventas

            when(stockHistoryFindLastService.execute(stock)).thenReturn(lastHistory);
            when(stockHistoryCalculateTrendService.execute(stock)).thenReturn(true);

            // When - Calcular variación
            Double result = stockCalculateVariationService.execute(stock);

            // Then - Debe retornar valor dentro del rango esperado
            verify(stockHistoryFindLastService).execute(stock);
            verify(stockHistoryCalculateTrendService).execute(stock);
            // Tendencia alcista: se limita el alza (max/2) para evitar momentum sostenido
            assertThat(result).isNotNull();
            // Verificamos que está dentro de un rango razonable basado en el nivel de riesgo
            assertThat(Math.abs(result)).isLessThanOrEqualTo(stock.getRiskLevel().getValor() * 2.0);
        }

        @Test
        @DisplayName("Given stock with bearish trend When calculating variation Then returns value within expected range")
        void whenBearishTrend_returnsValueWithinRange() {
            // Given - Acción con tendencia bajista
            Stock stock = InvestmentTestMother.defaultStock();
            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();
            lastHistory.setSoldAmount(BigInteger.valueOf(150));
            stock.setSoldAmount(BigInteger.valueOf(100)); // Decremento de ventas

            when(stockHistoryFindLastService.execute(stock)).thenReturn(lastHistory);
            when(stockHistoryCalculateTrendService.execute(stock)).thenReturn(false);

            // When - Calcular variación
            Double result = stockCalculateVariationService.execute(stock);

            // Then - Debe retornar valor dentro del rango esperado
            verify(stockHistoryFindLastService).execute(stock);
            verify(stockHistoryCalculateTrendService).execute(stock);
            // Tendencia bajista: se limita la caida (min/2) para evitar momentum sostenido
            assertThat(result).isNotNull();
            assertThat(Math.abs(result)).isLessThanOrEqualTo(stock.getRiskLevel().getValor() * 2.0);
        }

        @Test
        @DisplayName("Given stock with different risk levels When calculating variation Then returns values within respective ranges")
        void whenDifferentRiskLevels_returnsValuesWithinRanges() {
            // Given - Acciones con diferentes niveles de riesgo
            Stock lowRiskStock = InvestmentTestMother.stock(1001L, "Acción Bajo Riesgo", "ABR",
                    InvestmentTestMother.DEFAULT_INITIAL_PRICE, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT,
                    InvestmentTestMother.DEFAULT_SOLD_AMOUNT, RiskLevel.BAJO);
            Stock highRiskStock = InvestmentTestMother.stock(1002L, "Acción Alto Riesgo", "AAR",
                    InvestmentTestMother.DEFAULT_INITIAL_PRICE, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT,
                    InvestmentTestMother.DEFAULT_SOLD_AMOUNT, RiskLevel.ALTO);

            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();

            when(stockHistoryFindLastService.execute(lowRiskStock)).thenReturn(lastHistory);
            when(stockHistoryFindLastService.execute(highRiskStock)).thenReturn(lastHistory);
            when(stockHistoryCalculateTrendService.execute(lowRiskStock)).thenReturn(true);
            when(stockHistoryCalculateTrendService.execute(highRiskStock)).thenReturn(true);

            // When - Calcular variación
            Double lowRiskResult = stockCalculateVariationService.execute(lowRiskStock);
            Double highRiskResult = stockCalculateVariationService.execute(highRiskStock);

            // Then - Debe retornar valores dentro de rangos respectivos
            assertThat(lowRiskResult).isNotNull();
            assertThat(highRiskResult).isNotNull();
            // El rango de variación debería ser proporcional al nivel de riesgo
            assertThat(Math.abs(highRiskResult)).isGreaterThanOrEqualTo(Math.abs(lowRiskResult) - 5.0);
        }

        @Test
        @DisplayName("Given stock near price floor When calculating variation Then average tends positive")
        void whenNearPriceFloor_averageVariationIsPositive() {
            Stock stock = InvestmentTestMother.stock(1003L, "Acción Piso", "AP",
                    1000.0, 10.0,
                    InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT,
                    InvestmentTestMother.DEFAULT_SOLD_AMOUNT, RiskLevel.MEDIO);
            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();
            lastHistory.setSoldAmount(stock.getSoldAmount());

            when(stockHistoryFindLastService.execute(stock)).thenReturn(lastHistory);
            when(stockHistoryCalculateTrendService.execute(stock)).thenReturn(false);

            double sum = 0;
            int iterations = 500;
            for (int i = 0; i < iterations; i++) {
                sum += stockCalculateVariationService.execute(stock);
            }

            assertThat(sum / iterations).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("Given stock near price ceiling When calculating variation Then average tends negative")
        void whenNearPriceCeiling_averageVariationIsNegative() {
            Stock stock = InvestmentTestMother.stock(1004L, "Acción Techo", "AT",
                    1000.0, 2500.0,
                    InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, InvestmentTestMother.DEFAULT_AVAILABLE_AMOUNT,
                    InvestmentTestMother.DEFAULT_SOLD_AMOUNT, RiskLevel.MEDIO);
            StockHistory lastHistory = InvestmentTestMother.defaultStockHistory();
            lastHistory.setSoldAmount(stock.getSoldAmount());

            when(stockHistoryFindLastService.execute(stock)).thenReturn(lastHistory);
            when(stockHistoryCalculateTrendService.execute(stock)).thenReturn(true);

            double sum = 0;
            int iterations = 500;
            for (int i = 0; i < iterations; i++) {
                sum += stockCalculateVariationService.execute(stock);
            }

            assertThat(sum / iterations).isLessThan(0.0);
        }
    }
}

