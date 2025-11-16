package trinity.play2learn.backend.economy.transaction.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

@ExtendWith(MockitoExtension.class)
class TransactionGetLastTransactionsServiceTest {

    @Mock
    private ITransactionRepository transactionRepository;

    private TransactionGetLastTransactionsService transactionGetLastTransactionsService;

    @BeforeEach
    void setUp() {
        transactionGetLastTransactionsService = new TransactionGetLastTransactionsService(transactionRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet with transactions When getting last transactions Then returns list of transactions ordered by date descending")
        void whenWalletWithTransactions_returnsListOrderedByDateDesc() {
            // Given - Wallet con transacciones
            Wallet wallet = EconomyTestMother.defaultWallet();
            List<Transaction> expectedTransactions = Arrays.asList(
                EconomyTestMother.defaultTransaction(),
                EconomyTestMother.transactionWithDescription(
                    EconomyTestMother.DEFAULT_TRANSACTION_ID + 1,
                    "Recompensa por actividad completada",
                    EconomyTestMother.AMOUNT_MEDIUM
                )
            );

            when(transactionRepository.findTop10ByWalletOrderByCreatedAtDesc(wallet))
                .thenReturn(expectedTransactions);

            // When - Obtener últimas transacciones
            List<Transaction> result = transactionGetLastTransactionsService.execute(wallet);

            // Then - Debe retornar lista de transacciones ordenadas por fecha descendente
            verify(transactionRepository).findTop10ByWalletOrderByCreatedAtDesc(wallet);

            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(expectedTransactions);
        }

        @Test
        @DisplayName("Given wallet with no transactions When getting last transactions Then returns empty list")
        void whenWalletWithNoTransactions_returnsEmptyList() {
            // Given - Wallet sin transacciones
            Wallet wallet = EconomyTestMother.defaultWallet();
            List<Transaction> expectedTransactions = Collections.emptyList();

            when(transactionRepository.findTop10ByWalletOrderByCreatedAtDesc(wallet))
                .thenReturn(expectedTransactions);

            // When - Obtener últimas transacciones
            List<Transaction> result = transactionGetLastTransactionsService.execute(wallet);

            // Then - Debe retornar lista vacía
            verify(transactionRepository).findTop10ByWalletOrderByCreatedAtDesc(wallet);

            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Given wallet with more than 10 transactions When getting last transactions Then returns only top 10")
        void whenWalletWithMoreThan10Transactions_returnsOnlyTop10() {
            // Given - Wallet con más de 10 transacciones
            Wallet wallet = EconomyTestMother.defaultWallet();
            List<Transaction> expectedTransactions = EconomyTestMother.transactionList(
                Arrays.asList(
                    "Transacción 1", "Transacción 2", "Transacción 3", "Transacción 4", "Transacción 5",
                    "Transacción 6", "Transacción 7", "Transacción 8", "Transacción 9", "Transacción 10"
                ),
                Arrays.asList(
                    EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM
                )
            );

            when(transactionRepository.findTop10ByWalletOrderByCreatedAtDesc(wallet))
                .thenReturn(expectedTransactions);

            // When - Obtener últimas transacciones
            List<Transaction> result = transactionGetLastTransactionsService.execute(wallet);

            // Then - Debe retornar solo las 10 primeras
            verify(transactionRepository).findTop10ByWalletOrderByCreatedAtDesc(wallet);

            assertThat(result).isNotNull();
            assertThat(result).hasSize(10);
        }

        @Test
        @DisplayName("Given wallet with exactly 10 transactions When getting last transactions Then returns all 10 transactions")
        void whenWalletWithExactly10Transactions_returnsAll10Transactions() {
            // Given - Wallet con exactamente 10 transacciones
            Wallet wallet = EconomyTestMother.defaultWallet();
            List<Transaction> expectedTransactions = EconomyTestMother.transactionList(
                Arrays.asList(
                    "Transacción 1", "Transacción 2", "Transacción 3", "Transacción 4", "Transacción 5",
                    "Transacción 6", "Transacción 7", "Transacción 8", "Transacción 9", "Transacción 10"
                ),
                Arrays.asList(
                    EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM, EconomyTestMother.AMOUNT_MEDIUM,
                    EconomyTestMother.AMOUNT_MEDIUM
                )
            );

            when(transactionRepository.findTop10ByWalletOrderByCreatedAtDesc(wallet))
                .thenReturn(expectedTransactions);

            // When - Obtener últimas transacciones
            List<Transaction> result = transactionGetLastTransactionsService.execute(wallet);

            // Then - Debe retornar las 10 transacciones
            verify(transactionRepository).findTop10ByWalletOrderByCreatedAtDesc(wallet);

            assertThat(result).isNotNull();
            assertThat(result).hasSize(10);
            assertThat(result).containsExactlyElementsOf(expectedTransactions);
        }
    }
}

