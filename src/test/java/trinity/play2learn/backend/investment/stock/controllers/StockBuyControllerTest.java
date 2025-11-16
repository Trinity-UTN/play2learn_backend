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
import trinity.play2learn.backend.investment.stock.dtos.request.StockBuyRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockBuyService;
import trinity.play2learn.backend.user.models.User;

@WebMvcTest(StockBuyController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockBuyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStockBuyService stockBuyService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /investment/stocks/buy")
    class PostBuy {

        @Test
        @DisplayName("Given valid stock buy request When buying Then returns 201 Created with buy response")
        void whenValidRequest_returnsCreatedWithResponse() throws Exception {
            // Given - Request válido
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, InvestmentTestMother.DEFAULT_QUANTITY);
            StockBuyResponseDto responseDto = InvestmentTestMother.stockBuyResponseDto(
                    InvestmentTestMother.DEFAULT_ORDER_ID, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    InvestmentTestMother.DEFAULT_QUANTITY,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE * InvestmentTestMother.DEFAULT_QUANTITY.doubleValue());

            Mockito.when(stockBuyService.cu84buystocks(Mockito.any(), Mockito.any(User.class)))
                    .thenReturn(responseDto);

            // When & Then - POST debe retornar 201 con respuesta
            // Motivo: Verificar que el controller delega correctamente al servicio y retorna la respuesta esperada
            mockMvc.perform(post("/investment/stocks/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(InvestmentTestMother.DEFAULT_ORDER_ID))
                    .andExpect(jsonPath("$.data.pricePerUnit").value(InvestmentTestMother.DEFAULT_CURRENT_PRICE))
                    .andExpect(jsonPath("$.data.quantity").exists())
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockBuyService).cu84buystocks(Mockito.any(), Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given invalid stock buy request When buying Then returns 400 Bad Request")
        void whenInvalidRequest_returnsBadRequest() throws Exception {
            // Given - Request inválido (cantidad cero)
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, BigInteger.ZERO);

            // When & Then - POST debe retornar 400
            // Motivo: Verificar que las validaciones de Bean Validation funcionan correctamente
            mockMvc.perform(post("/investment/stocks/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given non-existing stock When buying Then returns 404 Not Found")
        void whenNonExistingStock_returnsNotFound() throws Exception {
            // Given - Request válido pero stock no existe
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, InvestmentTestMother.DEFAULT_QUANTITY);

            Mockito.when(stockBuyService.cu84buystocks(Mockito.any(), Mockito.any(User.class)))
                    .thenThrow(new NotFoundException("Accion no encontrada"));

            // When & Then - POST debe retornar 404
            // Motivo: Verificar que el controller maneja correctamente excepciones de recurso no encontrado (NotFoundException)
            mockMvc.perform(post("/investment/stocks/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Given insufficient wallet balance When buying Then returns 400 Bad Request")
        void whenInsufficientBalance_returnsBadRequest() throws Exception {
            // Given - Request válido pero balance insuficiente
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, InvestmentTestMother.DEFAULT_QUANTITY);

            Mockito.when(stockBuyService.cu84buystocks(Mockito.any(), Mockito.any(User.class)))
                    .thenThrow(new BadRequestException("No hay suficiente balance en el wallet"));

            // When & Then - POST debe retornar 400
            // Motivo: Verificar que el controller maneja correctamente excepciones de validación de negocio (BadRequestException)
            mockMvc.perform(post("/investment/stocks/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given insufficient stock availability When buying Then returns 400 Bad Request")
        void whenInsufficientAvailability_returnsBadRequest() throws Exception {
            // Given - Request válido pero disponibilidad insuficiente
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_ID, BigInteger.valueOf(1000));

            Mockito.when(stockBuyService.cu84buystocks(Mockito.any(), Mockito.any(User.class)))
                    .thenThrow(new BadRequestException("No hay suficientes acciones disponibles"));

            // When & Then - POST debe retornar 400
            // Motivo: Verificar que el controller maneja correctamente excepciones de validación de negocio (BadRequestException)
            mockMvc.perform(post("/investment/stocks/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}

