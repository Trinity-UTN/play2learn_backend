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

import trinity.play2learn.backend.configs.interceptors.SessionUserArgumentResolver;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletCompleteResponseDto;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetService;
import trinity.play2learn.backend.user.models.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletGetController.class)
@AutoConfigureMockMvc(addFilters = false)
class WalletGetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IWalletGetService walletGetService;

    @MockBean
    private SessionUserArgumentResolver sessionUserArgumentResolver;

    @Nested
    @DisplayName("GET /wallet")
    class GetWallet {

        @Test
        @DisplayName("Given authenticated student When getting wallet Then returns 200 OK with wallet complete response")
        void whenAuthenticatedStudent_returnsOkWithWalletCompleteResponse() throws Exception {
            // Given - Usuario estudiante autenticado y wallet completo
            WalletCompleteResponseDto responseDto = EconomyTestMother.walletCompleteResponseDto(
                EconomyTestMother.DEFAULT_WALLET_ID,
                EconomyTestMother.AMOUNT_LARGE,
                EconomyTestMother.AMOUNT_MEDIUM,
                EconomyTestMother.AMOUNT_LARGE + EconomyTestMother.AMOUNT_MEDIUM,
                java.util.Arrays.asList(
                    EconomyTestMother.transactionResponseDto(
                        EconomyTestMother.AMOUNT_MEDIUM,
                        java.time.LocalDateTime.now(),
                        "Compra de beneficio",
                        "COMPRA"
                    ),
                    EconomyTestMother.transactionResponseDto(
                        EconomyTestMother.AMOUNT_SMALL,
                        java.time.LocalDateTime.now(),
                        "Recompensa por actividad",
                        "RECOMPENSA"
                    )
                )
            );

            Mockito.when(walletGetService.cu70GetWallet(Mockito.any(User.class)))
                .thenReturn(responseDto);

            // When & Then - GET /wallet debe retornar 200 con datos del wallet
            mockMvc.perform(get("/wallet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(EconomyTestMother.DEFAULT_WALLET_ID))
                .andExpect(jsonPath("$.data.balance").value(EconomyTestMother.AMOUNT_LARGE))
                .andExpect(jsonPath("$.data.invertedBalance").value(EconomyTestMother.AMOUNT_MEDIUM))
                .andExpect(jsonPath("$.data.totalBalance").value(EconomyTestMother.AMOUNT_LARGE + EconomyTestMother.AMOUNT_MEDIUM))
                .andExpect(jsonPath("$.data.transactions").isArray())
                .andExpect(jsonPath("$.data.transactions.length()").value(2))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(walletGetService).cu70GetWallet(Mockito.any(User.class));
        }

        @Test
        @DisplayName("Given authenticated student with empty transactions When getting wallet Then returns 200 OK with wallet without transactions")
        void whenAuthenticatedStudentWithEmptyTransactions_returnsOkWithWalletWithoutTransactions() throws Exception {
            // Given - Usuario estudiante autenticado y wallet sin transacciones
            WalletCompleteResponseDto responseDto = EconomyTestMother.walletCompleteResponseDto(
                EconomyTestMother.DEFAULT_WALLET_ID,
                EconomyTestMother.AMOUNT_LARGE,
                EconomyTestMother.AMOUNT_MEDIUM,
                EconomyTestMother.AMOUNT_LARGE + EconomyTestMother.AMOUNT_MEDIUM,
                java.util.Collections.emptyList()
            );

            Mockito.when(walletGetService.cu70GetWallet(Mockito.any(User.class)))
                .thenReturn(responseDto);

            // When & Then - GET /wallet debe retornar 200 con wallet sin transacciones
            mockMvc.perform(get("/wallet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(EconomyTestMother.DEFAULT_WALLET_ID))
                .andExpect(jsonPath("$.data.transactions").isArray())
                .andExpect(jsonPath("$.data.transactions.length()").value(0))
                .andExpect(jsonPath("$.message").exists());

            // Verify - Servicio fue llamado correctamente
            Mockito.verify(walletGetService).cu70GetWallet(Mockito.any(User.class));
        }
    }
}

