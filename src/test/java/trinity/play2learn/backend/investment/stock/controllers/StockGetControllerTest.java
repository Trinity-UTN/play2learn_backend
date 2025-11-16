package trinity.play2learn.backend.investment.stock.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockGetService;
import trinity.play2learn.backend.user.models.User;

@WebMvcTest(StockGetController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockGetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStockGetService stockGetService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /investment/stocks/{id}")
    class GetStock {

        @Test
        @DisplayName("Given valid stock id When getting stock Then returns 200 OK with stock response")
        void whenValidId_returnsOkWithResponse() throws Exception {
            // Given - ID válido
            Long id = InvestmentTestMother.DEFAULT_STOCK_ID;
            StockResponseDto responseDto = InvestmentTestMother.defaultStockResponseDto();

            Mockito.when(stockGetService.cu100GetStock(Mockito.eq(id), Mockito.any(User.class)))
                    .thenReturn(responseDto);

            // When & Then - GET debe retornar 200 con respuesta
            // Motivo: Verificar que el controller delega correctamente al servicio y retorna la respuesta esperada
            mockMvc.perform(get("/investment/stocks/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(InvestmentTestMother.DEFAULT_STOCK_ID))
                    .andExpect(jsonPath("$.data.name").value(InvestmentTestMother.DEFAULT_STOCK_NAME))
                    .andExpect(jsonPath("$.data.abbreviation").value(InvestmentTestMother.DEFAULT_STOCK_ABBREVIATION))
                    .andExpect(jsonPath("$.data.currentPrice").value(InvestmentTestMother.DEFAULT_CURRENT_PRICE))
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockGetService).cu100GetStock(Mockito.eq(id), Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given non-existing stock id When getting stock Then returns 404 Not Found")
        void whenNonExistingId_returnsNotFound() throws Exception {
            // Given - ID no existente
            Long id = 999L;

            Mockito.when(stockGetService.cu100GetStock(Mockito.eq(id), Mockito.any(User.class)))
                    .thenThrow(new NotFoundException("Accion no encontrada"));

            // When & Then - GET debe retornar 404
            // Motivo: Verificar que el controller maneja correctamente excepciones de recurso no encontrado (NotFoundException)
            mockMvc.perform(get("/investment/stocks/{id}", id))
                    .andExpect(status().isNotFound());
        }
    }
}

