package trinity.play2learn.backend.economy.wallet.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionResponseDto;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGetLastTransaccionsService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class WalletGetLastTransactionsServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private ITransactionGetLastTransaccionsService transactionGetLastTransaccionsService;

    private WalletGetLastTransactionsService walletGetLastTransactionsService;

    @BeforeEach
    void setUp() {
        walletGetLastTransactionsService = new WalletGetLastTransactionsService(
            studentGetByEmailService,
            transactionGetLastTransaccionsService
        );
    }

    @Nested
    @DisplayName("cu65GetLastTransactions")
    class GetLastTransactions {

        @Test
        @DisplayName("Given user with wallet and transactions When getting last transactions Then returns list of transaction DTOs")
        void whenUserWithWalletAndTransactions_returnsListOfTransactionDtos() {
            // Given - Usuario con wallet y transacciones
            User user = EconomyTestMother.studentUser(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = EconomyTestMother.student(EconomyTestMother.DEFAULT_STUDENT_ID, EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            List<Transaction> transactions = Arrays.asList(
                EconomyTestMother.defaultTransaction()
            );

            when(studentGetByEmailService.getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(transactionGetLastTransaccionsService.execute(student.getWallet())).thenReturn(transactions);

            // When - Obtener últimas transacciones
            List<TransactionResponseDto> result = walletGetLastTransactionsService.cu65GetLastTransactions(user);

            // Then - Debe retornar lista de DTOs de transacciones
            verify(studentGetByEmailService).getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            verify(transactionGetLastTransaccionsService).execute(student.getWallet());

            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Given user with wallet and no transactions When getting last transactions Then returns empty list")
        void whenUserWithWalletAndNoTransactions_returnsEmptyList() {
            // Given - Usuario con wallet pero sin transacciones
            User user = EconomyTestMother.studentUser(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = EconomyTestMother.student(EconomyTestMother.DEFAULT_STUDENT_ID, EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            List<Transaction> transactions = List.of();

            when(studentGetByEmailService.getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(transactionGetLastTransaccionsService.execute(student.getWallet())).thenReturn(transactions);

            // When - Obtener últimas transacciones
            List<TransactionResponseDto> result = walletGetLastTransactionsService.cu65GetLastTransactions(user);

            // Then - Debe retornar lista vacía
            verify(studentGetByEmailService).getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            verify(transactionGetLastTransaccionsService).execute(student.getWallet());

            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Given user with wallet and multiple transactions When getting last transactions Then returns list with all transaction DTOs")
        void whenUserWithWalletAndMultipleTransactions_returnsListWithAllTransactionDtos() {
            // Given - Usuario con wallet y múltiples transacciones
            User user = EconomyTestMother.studentUser(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = EconomyTestMother.student(EconomyTestMother.DEFAULT_STUDENT_ID, EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            List<Transaction> transactions = Arrays.asList(
                EconomyTestMother.defaultTransaction(),
                EconomyTestMother.transactionWithDescription(
                    EconomyTestMother.DEFAULT_TRANSACTION_ID + 1,
                    "Recompensa por actividad completada",
                    200.0
                )
            );

            when(studentGetByEmailService.getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(transactionGetLastTransaccionsService.execute(student.getWallet())).thenReturn(transactions);

            // When - Obtener últimas transacciones
            List<TransactionResponseDto> result = walletGetLastTransactionsService.cu65GetLastTransactions(user);

            // Then - Debe retornar lista con todas las transacciones
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
        }
    }
}

