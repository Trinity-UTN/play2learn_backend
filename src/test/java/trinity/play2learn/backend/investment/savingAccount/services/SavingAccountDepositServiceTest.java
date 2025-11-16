package trinity.play2learn.backend.investment.savingAccount.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountDepositRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountFindByIdService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class SavingAccountDepositServiceTest {

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

    private SavingAccountDepositService savingAccountDepositService;

    @BeforeEach
    void setUp() {
        savingAccountDepositService = new SavingAccountDepositService(
            studentGetByEmailService,
            savingAccountFindByIdService,
            savingAccountRepository,
            transactionGenerateService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu103depositSavingAccount")
    class Cu103depositSavingAccount {

        @Test
        @DisplayName("Given valid request and sufficient wallet balance When depositing Then increments current amount and generates transaction")
        void whenValidRequestAndSufficientBalance_incrementsAmountAndGeneratesTransaction() {
            // Given - Request válido y wallet con balance suficiente
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            SavingAccount savingAccount = InvestmentTestMother.defaultSavingAccount();
            savingAccount.setWallet(wallet);
            SavingAccountDepositRequestDto requestDto = SavingAccountDepositRequestDto.builder()
                    .id(savingAccount.getId())
                    .amount(InvestmentTestMother.AMOUNT_MEDIUM)
                    .build();
            Double expectedCurrentAmount = savingAccount.getCurrentAmount() + requestDto.getAmount();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountFindByIdService.execute(requestDto.getId())).thenReturn(savingAccount);
            when(savingAccountRepository.save(any(SavingAccount.class))).thenAnswer(invocation -> {
                SavingAccount saved = invocation.getArgument(0);
                saved.setCurrentAmount(expectedCurrentAmount);
                return saved;
            });

            // When - Depositar en caja de ahorro
            SavingAccountResponseDto result = savingAccountDepositService.cu103depositSavingAccount(requestDto, user);

            // Then - Debe incrementar monto actual y generar transacción
            ArgumentCaptor<SavingAccount> accountCaptor = ArgumentCaptor.forClass(SavingAccount.class);
            verify(savingAccountRepository).save(accountCaptor.capture());

            SavingAccount capturedAccount = accountCaptor.getValue();
            assertThat(capturedAccount.getCurrentAmount()).isEqualTo(expectedCurrentAmount);

            verify(transactionGenerateService).generate(eq(TypeTransaction.INGRESO_CAJA_AHORRO),
                    eq(requestDto.getAmount()), eq("Deposito en caja de ahorro"), eq(TransactionActor.ESTUDIANTE),
                    eq(TransactionActor.SISTEMA), eq(wallet), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(savingAccount));
            verify(walletUpdateInvestedBalanceService).execute(wallet);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Given saving account not belonging to student When depositing Then throws ConflictException")
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
            SavingAccountDepositRequestDto requestDto = SavingAccountDepositRequestDto.builder()
                    .id(savingAccount.getId())
                    .amount(InvestmentTestMother.AMOUNT_MEDIUM)
                    .build();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountFindByIdService.execute(requestDto.getId())).thenReturn(savingAccount);

            // When & Then - Debe lanzar ConflictException
            assertThatThrownBy(() -> savingAccountDepositService.cu103depositSavingAccount(requestDto, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("La cuenta de ahorro no pertenece al estudiante");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(savingAccountRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }

        @Test
        @DisplayName("Given wallet with insufficient balance When depositing Then throws BadRequestException")
        void whenInsufficientBalance_throwsBadRequestException() {
            // Given - Wallet sin balance suficiente
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.walletWithInsufficientBalance(InvestmentTestMother.DEFAULT_WALLET_ID,
                    InvestmentTestMother.AMOUNT_SMALL, InvestmentTestMother.AMOUNT_MEDIUM);
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            SavingAccount savingAccount = InvestmentTestMother.defaultSavingAccount();
            savingAccount.setWallet(wallet);
            SavingAccountDepositRequestDto requestDto = SavingAccountDepositRequestDto.builder()
                    .id(savingAccount.getId())
                    .amount(InvestmentTestMother.AMOUNT_MEDIUM)
                    .build();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountFindByIdService.execute(requestDto.getId())).thenReturn(savingAccount);

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> savingAccountDepositService.cu103depositSavingAccount(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No hay saldo suficiente en la wallet");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(savingAccountRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }
    }
}

