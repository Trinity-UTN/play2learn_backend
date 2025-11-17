package trinity.play2learn.backend.investment.stock.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockRegisterService;

@WebMvcTest(StockRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStockRegisterService stockRegisterService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /investment/stocks")
    class PostStock {

        @Test
        @DisplayName("Given valid stock request When registering Then returns 201 Created with stock response")
        void whenValidRequest_returnsCreatedWithResponse() throws Exception {
            // Given - Request válido
            StockRegisterRequestDto requestDto = InvestmentTestMother.stockRegisterRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_NAME, InvestmentTestMother.DEFAULT_STOCK_ABBREVIATION,
                    InvestmentTestMother.DEFAULT_INITIAL_PRICE, InvestmentTestMother.DEFAULT_TOTAL_AMOUNT,
                    RiskLevel.MEDIO);
            StockResponseDto responseDto = InvestmentTestMother.defaultStockResponseDto();

            Mockito.when(stockRegisterService.cu77registerStock(Mockito.any())).thenReturn(responseDto);

            // When & Then - POST debe retornar 201 con respuesta
            mockMvc.perform(post("/investment/stocks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(InvestmentTestMother.DEFAULT_STOCK_ID))
                    .andExpect(jsonPath("$.data.name").value(InvestmentTestMother.DEFAULT_STOCK_NAME))
                    .andExpect(jsonPath("$.data.abbreviation").value(InvestmentTestMother.DEFAULT_STOCK_ABBREVIATION))
                    .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(stockRegisterService).cu77registerStock(Mockito.any());
        }

        @Test
        @DisplayName("Given invalid stock request When registering Then returns 400 Bad Request")
        void whenInvalidRequest_returnsBadRequest() throws Exception {
            // Given - Request inválido (precio negativo)
            StockRegisterRequestDto requestDto = InvestmentTestMother.stockRegisterRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_NAME, InvestmentTestMother.DEFAULT_STOCK_ABBREVIATION,
                    InvestmentTestMother.AMOUNT_NEGATIVE, InvestmentTestMother.DEFAULT_TOTAL_AMOUNT, RiskLevel.MEDIO);

            // When & Then - POST debe retornar 400
            mockMvc.perform(post("/investment/stocks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given duplicate stock abbreviation When registering Then returns 409 Conflict")
        void whenDuplicateAbbreviation_returnsConflict() throws Exception {
            // Given - Request válido pero abreviatura duplicada
            StockRegisterRequestDto requestDto = InvestmentTestMother.stockRegisterRequestDto(
                    InvestmentTestMother.DEFAULT_STOCK_NAME, InvestmentTestMother.DEFAULT_STOCK_ABBREVIATION,
                    InvestmentTestMother.DEFAULT_INITIAL_PRICE, InvestmentTestMother.DEFAULT_TOTAL_AMOUNT,
                    RiskLevel.MEDIO);

            Mockito.when(stockRegisterService.cu77registerStock(Mockito.any()))
                    .thenThrow(new ConflictException("Ya existe una acción con esa abreviatura"));

            // When & Then - POST debe retornar 409
            mockMvc.perform(post("/investment/stocks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isConflict());
        }
    }
}

