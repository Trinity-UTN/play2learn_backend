package trinity.play2learn.backend.investment.savingAccount.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountFindByIdService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class SavingAccountDeleteServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private ISavingAccountFindByIdService savingAccountFindByIdService;

    @Mock
    private ISavingAccountRepository savingAccountRepository;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    private SavingAccountDeleteService savingAccountDeleteService;

    @BeforeEach
    void setUp() {
        savingAccountDeleteService = new SavingAccountDeleteService(
            studentGetByEmailService,
            savingAccountFindByIdService,
            savingAccountRepository,
            transactionGenerateService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu105deleteSavingAccount")
    class Cu105deleteSavingAccount {

        @Test
        @DisplayName("Given saving account with zero balance When deleting Then performs soft delete without transaction")
        void whenZeroBalance_performsSoftDeleteWithoutTransaction() {
            // Given - Caja de ahorro con saldo cero
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            SavingAccount savingAccount = InvestmentTestMother.defaultSavingAccount();
            savingAccount.setWallet(wallet);
            savingAccount.setCurrentAmount(0.0);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountFindByIdService.execute(savingAccount.getId())).thenReturn(savingAccount);
            when(savingAccountRepository.save(any(SavingAccount.class))).thenAnswer(invocation -> {
                SavingAccount saved = invocation.getArgument(0);
                saved.setDeletedAt(LocalDateTime.now());
                return saved;
            });

            // When - Eliminar caja de ahorro
            savingAccountDeleteService.cu105deleteSavingAccount(savingAccount.getId(), user);

            // Then - Debe hacer soft delete sin generar transacción
            ArgumentCaptor<SavingAccount> accountCaptor = ArgumentCaptor.forClass(SavingAccount.class);
            verify(savingAccountRepository).save(accountCaptor.capture());

            SavingAccount capturedAccount = accountCaptor.getValue();
            assertThat(capturedAccount.getDeletedAt()).isNotNull();
            assertThat(capturedAccount.getCurrentAmount()).isZero();

            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any());
            verify(walletUpdateInvestedBalanceService).execute(wallet);
        }

        @Test
        @DisplayName("Given saving account with positive balance When deleting Then transfers balance and performs soft delete")
        void whenPositiveBalance_transfersBalanceAndPerformsSoftDelete() {
            // Given - Caja de ahorro con saldo positivo
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            SavingAccount savingAccount = InvestmentTestMother.defaultSavingAccount();
            savingAccount.setWallet(wallet);
            Double balanceToTransfer = savingAccount.getCurrentAmount();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountFindByIdService.execute(savingAccount.getId())).thenReturn(savingAccount);
            when(savingAccountRepository.save(any(SavingAccount.class))).thenAnswer(invocation -> {
                SavingAccount saved = invocation.getArgument(0);
                saved.setDeletedAt(LocalDateTime.now());
                saved.setCurrentAmount(0.0);
                return saved;
            });

            // When - Eliminar caja de ahorro
            savingAccountDeleteService.cu105deleteSavingAccount(savingAccount.getId(), user);

            // Then - Debe transferir saldo, generar transacción y hacer soft delete
            ArgumentCaptor<SavingAccount> accountCaptor = ArgumentCaptor.forClass(SavingAccount.class);
            verify(savingAccountRepository).save(accountCaptor.capture());

            SavingAccount capturedAccount = accountCaptor.getValue();
            assertThat(capturedAccount.getDeletedAt()).isNotNull();
            assertThat(capturedAccount.getCurrentAmount()).isZero();

            verify(transactionGenerateService).generate(eq(TypeTransaction.RETIRO_CAJA_AHORRO),
                    eq(balanceToTransfer), eq("Retiro de caja de ahorro"), eq(TransactionActor.SISTEMA),
                    eq(TransactionActor.ESTUDIANTE), eq(wallet), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(savingAccount));
            verify(walletUpdateInvestedBalanceService).execute(wallet);
        }

        @Test
        @DisplayName("Given saving account not belonging to student When deleting Then throws ConflictException")
        void whenAccountNotBelongingToStudent_throwsConflictException() {
            // Given - Caja de ahorro que no pertenece al estudiante
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Wallet otherWallet = InvestmentTestMother.wallet(999L, InvestmentTestMother.AMOUNT_LARGE,
                    InvestmentTestMother.AMOUNT_MEDIUM);
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            SavingAccount savingAccount = InvestmentTestMother.defaultSavingAccount();
            savingAccount.setWallet(otherWallet);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountFindByIdService.execute(savingAccount.getId())).thenReturn(savingAccount);

            // When & Then - Debe lanzar ConflictException
            org.junit.jupiter.api.Assertions.assertThrows(ConflictException.class,
                    () -> savingAccountDeleteService.cu105deleteSavingAccount(savingAccount.getId(), user),
                    "La cuenta de ahorro no pertenece al estudiante");

            // Verify - No debe llamar a servicios cuando hay excepción
            verify(savingAccountRepository, never()).save(any(SavingAccount.class));
            verify(transactionGenerateService, never()).generate(any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any());
            verify(walletUpdateInvestedBalanceService, never()).execute(any(Wallet.class));
        }
    }
}

