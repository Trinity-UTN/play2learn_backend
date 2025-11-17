package trinity.play2learn.backend.investment.stock.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListPaginatedService;

@WebMvcTest(StockListPaginatedController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockListPaginatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStockListPaginatedService stockListPaginatedService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /investment/stocks/paginated")
    class ListPaginated {

        @Test
        @DisplayName("Given stocks exist When listing paginated Then returns 200 OK with paginated data")
        void whenStocksExist_returnsOkWithPaginatedData() throws Exception {
            // Given - Acciones existentes
            List<StockResponseDto> stocks = Arrays.asList(
                InvestmentTestMother.defaultStockResponseDto()
            );

            PaginatedData<StockResponseDto> paginatedData = PaginatedData.<StockResponseDto>builder()
                .results(stocks)
                .count(1)
                .totalPages(1)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(stockListPaginatedService.cu87ListPaginatedStock(
                Mockito.eq(1), Mockito.eq(10), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then - GET debe retornar 200 con datos paginados
            mockMvc.perform(get("/investment/stocks/paginated")
                    .param("page", "1")
                    .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockListPaginatedService).cu87ListPaginatedStock(
                Mockito.eq(1), Mockito.eq(10), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Given no stocks exist When listing paginated Then returns 200 OK with empty paginated data")
        void whenNoStocksExist_returnsOkWithEmptyPaginatedData() throws Exception {
            // Given - Sin acciones
            PaginatedData<StockResponseDto> paginatedData = PaginatedData.<StockResponseDto>builder()
                .results(List.of())
                .count(0)
                .totalPages(0)
                .currentPage(1)
                .pageSize(10)
                .build();

            Mockito.when(stockListPaginatedService.cu87ListPaginatedStock(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(paginatedData);

            // When & Then - GET debe retornar 200 con datos paginados vacíos
            mockMvc.perform(get("/investment/stocks/paginated")
                    .param("page", "1")
                    .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0))
                .andExpect(jsonPath("$.message").exists());
        }
    }
}

