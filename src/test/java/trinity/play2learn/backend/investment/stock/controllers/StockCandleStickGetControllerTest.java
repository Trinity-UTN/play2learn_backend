package trinity.play2learn.backend.investment.stock.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.response.CandleStickChartValueResponseDto;
import trinity.play2learn.backend.investment.stock.models.RangeValue;
import trinity.play2learn.backend.investment.stock.services.interfaces.ICandleStickGetValuesService;

@WebMvcTest(StockCandleStickGetController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockCandleStickGetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICandleStickGetValuesService candleStickGetValuesService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /investment/stocks/candlestick")
    class GetCandleStick {

        @Test
        @DisplayName("Given valid stock id and range When getting candlestick values Then returns 200 OK with list of values")
        void whenValidIdAndRange_returnsOkWithList() throws Exception {
            // Given - ID válido y rango
            Long stockId = InvestmentTestMother.DEFAULT_STOCK_ID;
            RangeValue rangeValue = RangeValue.DIARIO;
            List<CandleStickChartValueResponseDto> values = Arrays.asList(
                InvestmentTestMother.candleStickChartValueResponseDto(LocalDate.now(), InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                        InvestmentTestMother.DEFAULT_CURRENT_PRICE + 5.0, InvestmentTestMother.DEFAULT_CURRENT_PRICE + 10.0,
                        InvestmentTestMother.DEFAULT_CURRENT_PRICE - 5.0)
            );

            Mockito.when(candleStickGetValuesService.cu83GetValuesCandleStick(stockId, rangeValue))
                    .thenReturn(values);

            // When & Then - GET debe retornar 200 con lista de valores
            mockMvc.perform(get("/investment/stocks/candlestick")
                    .param("stockId", stockId.toString())
                    .param("rangeValue", rangeValue.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].open").value(InvestmentTestMother.DEFAULT_CURRENT_PRICE))
                    .andExpect(jsonPath("$.data[0].close").exists())
                    .andExpect(jsonPath("$.data[0].high").exists())
                    .andExpect(jsonPath("$.data[0].low").exists())
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(candleStickGetValuesService).cu83GetValuesCandleStick(stockId, rangeValue);
        }

        @Test
        @DisplayName("Given valid stock id with no candlestick values When getting values Then returns 200 OK with empty list")
        void whenValidIdWithNoValues_returnsOkWithEmptyList() throws Exception {
            // Given - ID válido sin valores
            Long stockId = InvestmentTestMother.DEFAULT_STOCK_ID;
            RangeValue rangeValue = RangeValue.DIARIO;
            List<CandleStickChartValueResponseDto> values = Collections.emptyList();

            Mockito.when(candleStickGetValuesService.cu83GetValuesCandleStick(stockId, rangeValue))
                    .thenReturn(values);

            // When & Then - GET debe retornar 200 con lista vacía
            mockMvc.perform(get("/investment/stocks/candlestick")
                    .param("stockId", stockId.toString())
                    .param("rangeValue", rangeValue.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0))
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(candleStickGetValuesService).cu83GetValuesCandleStick(stockId, rangeValue);
        }
    }
}

