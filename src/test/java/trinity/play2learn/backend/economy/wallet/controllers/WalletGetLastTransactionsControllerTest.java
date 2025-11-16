package trinity.play2learn.backend.economy.wallet.controllers;

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
import trinity.play2learn.backend.economy.transaction.dtos.TransactionResponseDto;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetLastTransactionsService;
import trinity.play2learn.backend.user.models.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletGetLastTransactionsController.class)
@AutoConfigureMockMvc(addFilters = false)
class WalletGetLastTransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IWalletGetLastTransactionsService walletGetLastTransactionsService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /wallet/last-transactions")
    class GetLastTransactions {

        @Test
        @DisplayName("Given authenticated student When getting last transactions Then returns 200 OK with list of transactions")
        void whenAuthenticatedStudent_returnsOkWithListOfTransactions() throws Exception {
            // Given - Usuario estudiante autenticado y lista de transacciones
            List<TransactionResponseDto> responseDtos = Arrays.asList(
                EconomyTestMother.transactionResponseDto(
                    EconomyTestMother.AMOUNT_MEDIUM,
                    LocalDateTime.now(),
                    "Compra de beneficio",
                    "COMPRA"
                ),
                EconomyTestMother.transactionResponseDto(
                    EconomyTestMother.AMOUNT_SMALL,
                    LocalDateTime.now().minusDays(1),
                    "Recompensa por actividad",
                    "RECOMPENSA"
                )
            );

            Mockito.when(walletGetLastTransactionsService.cu65GetLastTransactions(Mockito.any(User.class)))
                .thenReturn(responseDtos);

            // When & Then - GET /wallet/last-transactions debe retornar 200 con lista de transacciones
            mockMvc.perform(get("/wallet/last-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].amount").value(EconomyTestMother.AMOUNT_MEDIUM))
                .andExpect(jsonPath("$.data[0].description").value("Compra de beneficio"))
                .andExpect(jsonPath("$.data[0].type").value("COMPRA"))
                .andExpect(jsonPath("$.data[1].amount").value(EconomyTestMother.AMOUNT_SMALL))
                .andExpect(jsonPath("$.data[1].description").value("Recompensa por actividad"))
                .andExpect(jsonPath("$.data[1].type").value("RECOMPENSA"))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(walletGetLastTransactionsService).cu65GetLastTransactions(Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given authenticated student with no transactions When getting last transactions Then returns 200 OK with empty list")
        void whenAuthenticatedStudentWithNoTransactions_returnsOkWithEmptyList() throws Exception {
            // Given - Usuario estudiante autenticado sin transacciones
            List<TransactionResponseDto> responseDtos = Collections.emptyList();

            Mockito.when(walletGetLastTransactionsService.cu65GetLastTransactions(Mockito.any(User.class)))
                .thenReturn(responseDtos);

            // When & Then - GET /wallet/last-transactions debe retornar 200 con lista vacía
            mockMvc.perform(get("/wallet/last-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(walletGetLastTransactionsService).cu65GetLastTransactions(Mockito.any(User.class));
        }
    }
}

