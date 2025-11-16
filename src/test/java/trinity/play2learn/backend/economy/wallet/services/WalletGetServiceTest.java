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
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGetLastTransaccionsService;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletCompleteResponseDto;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class WalletGetServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private ITransactionGetLastTransaccionsService transactionGetLastTransaccionsService;

    private WalletGetService walletGetService;

    @BeforeEach
    void setUp() {
        walletGetService = new WalletGetService(
            studentGetByEmailService,
            transactionGetLastTransaccionsService
        );
    }

    @Nested
    @DisplayName("cu70GetWallet")
    class GetWallet {

        @Test
        @DisplayName("Given user with wallet and transactions When getting wallet Then returns complete wallet DTO")
        void whenUserWithWalletAndTransactions_returnsCompleteWalletDto() {
            // Given - Usuario con wallet y transacciones
            User user = EconomyTestMother.studentUser(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = EconomyTestMother.student(EconomyTestMother.DEFAULT_STUDENT_ID, EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            List<Transaction> transactions = Arrays.asList(
                EconomyTestMother.defaultTransaction()
            );

            when(studentGetByEmailService.getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(transactionGetLastTransaccionsService.execute(student.getWallet())).thenReturn(transactions);

            // When - Obtener wallet completo
            WalletCompleteResponseDto result = walletGetService.cu70GetWallet(user);

            // Then - Debe retornar DTO completo con wallet y transacciones
            verify(studentGetByEmailService).getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            verify(transactionGetLastTransaccionsService).execute(student.getWallet());

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(student.getWallet().getId());
            assertThat(result.getBalance()).isEqualTo(student.getWallet().getBalance());
            assertThat(result.getInvertedBalance()).isEqualTo(student.getWallet().getInvertedBalance());
            assertThat(result.getTransactions()).isNotNull();
            assertThat(result.getTransactions()).hasSize(1);
        }

        @Test
        @DisplayName("Given user with wallet and no transactions When getting wallet Then returns complete wallet DTO with empty transactions list")
        void whenUserWithWalletAndNoTransactions_returnsCompleteWalletDtoWithEmptyList() {
            // Given - Usuario con wallet pero sin transacciones
            User user = EconomyTestMother.studentUser(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            Student student = EconomyTestMother.student(EconomyTestMother.DEFAULT_STUDENT_ID, EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            List<Transaction> transactions = List.of();

            when(studentGetByEmailService.getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL)).thenReturn(student);
            when(transactionGetLastTransaccionsService.execute(student.getWallet())).thenReturn(transactions);

            // When - Obtener wallet completo
            WalletCompleteResponseDto result = walletGetService.cu70GetWallet(user);

            // Then - Debe retornar DTO completo con lista vacía de transacciones
            verify(studentGetByEmailService).getByEmail(EconomyTestMother.DEFAULT_STUDENT_EMAIL);
            verify(transactionGetLastTransaccionsService).execute(student.getWallet());

            assertThat(result).isNotNull();
            assertThat(result.getTransactions()).isNotNull();
            assertThat(result.getTransactions()).isEmpty();
        }

        @Test
        @DisplayName("Given user with wallet and multiple transactions When getting wallet Then returns complete wallet DTO with all transactions")
        void whenUserWithWalletAndMultipleTransactions_returnsCompleteWalletDtoWithAllTransactions() {
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

            // When - Obtener wallet completo
            WalletCompleteResponseDto result = walletGetService.cu70GetWallet(user);

            // Then - Debe retornar DTO completo con todas las transacciones
            assertThat(result).isNotNull();
            assertThat(result.getTransactions()).hasSize(2);
        }
    }
}

