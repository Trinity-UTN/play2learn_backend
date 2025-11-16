package trinity.play2learn.backend.investment.stock.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.response.StockHistoryResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListHistoryService;

@WebMvcTest(StockHistoryListByStockController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockHistoryListByStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStockListHistoryService stockListHistoryService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /investment/stocks/histories/{id}")
    class GetStockHistories {

        @Test
        @DisplayName("Given valid stock id with history When getting histories Then returns 200 OK with list of histories")
        void whenValidIdWithHistory_returnsOkWithList() throws Exception {
            // Given - ID válido con historial
            Long id = InvestmentTestMother.DEFAULT_STOCK_ID;
            List<StockHistoryResponseDto> histories = Arrays.asList(
                InvestmentTestMother.stockHistoryResponseDto(InvestmentTestMother.DEFAULT_STOCK_HISTORY_ID,
                        InvestmentTestMother.DEFAULT_CURRENT_PRICE, 0.1, LocalDateTime.now())
            );

            Mockito.when(stockListHistoryService.cu79getStockHistories(id)).thenReturn(histories);

            // When & Then - GET debe retornar 200 con lista de historiales
            mockMvc.perform(get("/investment/stocks/histories/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].id").value(InvestmentTestMother.DEFAULT_STOCK_HISTORY_ID))
                    .andExpect(jsonPath("$.data[0].price").value(InvestmentTestMother.DEFAULT_CURRENT_PRICE))
                    .andExpect(jsonPath("$.data[0].variation").exists())
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockListHistoryService).cu79getStockHistories(id);
        }

        @Test
        @DisplayName("Given valid stock id with no history When getting histories Then returns 200 OK with empty list")
        void whenValidIdWithNoHistory_returnsOkWithEmptyList() throws Exception {
            // Given - ID válido sin historial
            Long id = InvestmentTestMother.DEFAULT_STOCK_ID;
            List<StockHistoryResponseDto> histories = Collections.emptyList();

            Mockito.when(stockListHistoryService.cu79getStockHistories(id)).thenReturn(histories);

            // When & Then - GET debe retornar 200 con lista vacía
            mockMvc.perform(get("/investment/stocks/histories/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0))
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockListHistoryService).cu79getStockHistories(id);
        }

        @Test
        @DisplayName("Given non-existing stock id When getting histories Then returns 404 Not Found")
        void whenNonExistingId_returnsNotFound() throws Exception {
            // Given - ID no existente
            Long id = 999L;

            Mockito.when(stockListHistoryService.cu79getStockHistories(id))
                    .thenThrow(new NotFoundException("Accion no encontrada"));

            // When & Then - GET debe retornar 404
            mockMvc.perform(get("/investment/stocks/histories/{id}", id))
                    .andExpect(status().isNotFound());
        }
    }
}

