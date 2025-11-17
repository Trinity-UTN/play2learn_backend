package trinity.play2learn.backend.investment.stock.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.request.StockOrderStopRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.models.OrderStop;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockRegisterStopService;
import trinity.play2learn.backend.user.models.User;

@WebMvcTest(StockRegisterStopController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockRegisterStopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStockRegisterStopService stockRegisterStopService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /investment/stocks/stop")
    class PostStop {

        @Test
        @DisplayName("Given valid stop order request When registering stop Then returns 201 Created with buy response")
        void whenValidRequest_returnsCreatedWithResponse() throws Exception {
            // Given - Request válido
            StockOrderStopRequestDto requestDto = InvestmentTestMother.stockOrderStopRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, InvestmentTestMother.DEFAULT_QUANTITY,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE, OrderStop.LOSS);
            StockBuyResponseDto responseDto = InvestmentTestMother.stockBuyResponseDto(
                    InvestmentTestMother.DEFAULT_ORDER_ID, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    InvestmentTestMother.DEFAULT_QUANTITY,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE * InvestmentTestMother.DEFAULT_QUANTITY.doubleValue());

            Mockito.when(stockRegisterStopService.cu91registerStopStock(Mockito.any(), Mockito.any(User.class)))
                    .thenReturn(responseDto);

            // When & Then - POST debe retornar 201 con respuesta
            mockMvc.perform(post("/investment/stocks/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(InvestmentTestMother.DEFAULT_ORDER_ID))
                    .andExpect(jsonPath("$.data.pricePerUnit").value(InvestmentTestMother.DEFAULT_CURRENT_PRICE))
                    .andExpect(jsonPath("$.data.quantity").exists())
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockRegisterStopService).cu91registerStopStock(Mockito.any(), Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given invalid stop order request When registering stop Then returns 400 Bad Request")
        void whenInvalidRequest_returnsBadRequest() throws Exception {
            // Given - Request inválido (cantidad cero)
            StockOrderStopRequestDto requestDto = InvestmentTestMother.stockOrderStopRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, BigInteger.ZERO,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE, OrderStop.LOSS);

            // When & Then - POST debe retornar 400
            mockMvc.perform(post("/investment/stocks/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given non-existing stock When registering stop Then returns 404 Not Found")
        void whenNonExistingStock_returnsNotFound() throws Exception {
            // Given - Request válido pero stock no existe
            StockOrderStopRequestDto requestDto = InvestmentTestMother.stockOrderStopRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, InvestmentTestMother.DEFAULT_QUANTITY,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE, OrderStop.LOSS);

            Mockito.when(stockRegisterStopService.cu91registerStopStock(Mockito.any(), Mockito.any(User.class)))
                    .thenThrow(new NotFoundException("Accion no encontrada"));

            // When & Then - POST debe retornar 404
            mockMvc.perform(post("/investment/stocks/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Given insufficient stock quantity in wallet When registering stop Then returns 400 Bad Request")
        void whenInsufficientQuantity_returnsBadRequest() throws Exception {
            // Given - Request válido pero cantidad insuficiente en wallet
            StockOrderStopRequestDto requestDto = InvestmentTestMother.stockOrderStopRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, BigInteger.valueOf(1000),
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE, OrderStop.LOSS);

            Mockito.when(stockRegisterStopService.cu91registerStopStock(Mockito.any(), Mockito.any(User.class)))
                    .thenThrow(new BadRequestException("No tienes suficientes acciones para registrar una orden de stop"));

            // When & Then - POST debe retornar 400
            mockMvc.perform(post("/investment/stocks/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}

