package trinity.play2learn.backend.economy.transaction.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionStatisticsResponseDto;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionListStatisticsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionStatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransactionListStatisticsService transactionListStatisticsService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /transactions")
    class GetStatistics {

        @Test
        @DisplayName("Given authenticated dev When getting statistics Then returns 200 OK with list of statistics")
        void whenAuthenticatedDev_returnsOkWithListOfStatistics() throws Exception {
            // Given - Estadísticas de transacciones
            LocalDateTime date = LocalDateTime.now();
            List<TransactionStatisticsResponseDto> responseDtos = Arrays.asList(
                EconomyTestMother.transactionStatisticsResponseDto(
                    date,
                    EconomyTestMother.AMOUNT_LARGE,
                    EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_SMALL,
                    EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_SMALL
                )
            );

            Mockito.when(transactionListStatisticsService.execute())
                .thenReturn(responseDtos);

            // When & Then - GET /transactions debe retornar 200 con lista de estadísticas
            mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].total").value(EconomyTestMother.AMOUNT_LARGE))
                .andExpect(jsonPath("$.data[0].totalCirculation").value(EconomyTestMother.AMOUNT_MEDIUM))
                .andExpect(jsonPath("$.data[0].totalReserve").value(EconomyTestMother.AMOUNT_SMALL))
                .andExpect(jsonPath("$.data[0].totalSubject").value(EconomyTestMother.AMOUNT_MEDIUM))
                .andExpect(jsonPath("$.data[0].totalStudent").value(EconomyTestMother.AMOUNT_MEDIUM))
                .andExpect(jsonPath("$.data[0].totalActivity").value(EconomyTestMother.AMOUNT_SMALL))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(transactionListStatisticsService).execute();
        }

        @Test
        @DisplayName("Given authenticated dev with multiple statistics When getting statistics Then returns 200 OK with multiple statistics")
        void whenAuthenticatedDevWithMultipleStatistics_returnsOkWithMultipleStatistics() throws Exception {
            // Given - Múltiples estadísticas de transacciones
            LocalDateTime date1 = LocalDateTime.now();
            LocalDateTime date2 = LocalDateTime.now().minusDays(1);
            
            List<TransactionStatisticsResponseDto> responseDtos = Arrays.asList(
                EconomyTestMother.transactionStatisticsResponseDto(
                    date1,
                    EconomyTestMother.AMOUNT_LARGE,
                    EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_SMALL,
                    EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_SMALL
                ),
                EconomyTestMother.transactionStatisticsResponseDto(
                    date2,
                    EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_SMALL,
                    EconomyTestMother.AMOUNT_SMALL,
                    EconomyTestMother.AMOUNT_SMALL,
                    EconomyTestMother.AMOUNT_SMALL,
                    EconomyTestMother.AMOUNT_MINIMUM
                )
            );

            Mockito.when(transactionListStatisticsService.execute())
                .thenReturn(responseDtos);

            // When & Then - GET /transactions debe retornar 200 con múltiples estadísticas
            mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].total").value(EconomyTestMother.AMOUNT_LARGE))
                .andExpect(jsonPath("$.data[1].total").value(EconomyTestMother.AMOUNT_MEDIUM))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(transactionListStatisticsService).execute();
        }

        @Test
        @DisplayName("Given authenticated dev with no statistics When getting statistics Then returns 200 OK with empty list")
        void whenAuthenticatedDevWithNoStatistics_returnsOkWithEmptyList() throws Exception {
            // Given - Sin estadísticas de transacciones
            List<TransactionStatisticsResponseDto> responseDtos = Collections.emptyList();

            Mockito.when(transactionListStatisticsService.execute())
                .thenReturn(responseDtos);

            // When & Then - GET /transactions debe retornar 200 con lista vacía
            mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(transactionListStatisticsService).execute();
        }
    }
}

