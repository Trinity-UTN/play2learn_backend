package trinity.play2learn.backend.investment.stock.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListService;

@WebMvcTest(StockListController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStockListService stockListService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /investment/stocks")
    class GetStocks {

        @Test
        @DisplayName("Given stocks exist When listing Then returns 200 OK with list of stocks")
        void whenStocksExist_returnsOkWithList() throws Exception {
            // Given - Acciones existentes
            List<StockResponseDto> stocks = Arrays.asList(
                InvestmentTestMother.defaultStockResponseDto()
            );

            Mockito.when(stockListService.cu86listStocks()).thenReturn(stocks);

            // When & Then - GET debe retornar 200 con lista de acciones
            mockMvc.perform(get("/investment/stocks"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].id").value(InvestmentTestMother.DEFAULT_STOCK_ID))
                    .andExpect(jsonPath("$.data[0].name").value(InvestmentTestMother.DEFAULT_STOCK_NAME))
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockListService).cu86listStocks();
        }

        @Test
        @DisplayName("Given no stocks exist When listing Then returns 200 OK with empty list")
        void whenNoStocksExist_returnsOkWithEmptyList() throws Exception {
            // Given - Sin acciones
            List<StockResponseDto> stocks = Collections.emptyList();

            Mockito.when(stockListService.cu86listStocks()).thenReturn(stocks);

            // When & Then - GET debe retornar 200 con lista vacía
            mockMvc.perform(get("/investment/stocks"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0))
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockListService).cu86listStocks();
        }
    }
}

