package trinity.play2learn.backend.investment.savingAccount.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountRegisterRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountExistsByNameAndWalletService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class SavingAccountRegisterServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private ISavingAccountExistsByNameAndWalletService savingAccountExistsByNameAndWalletService;

    @Mock
    private ISavingAccountRepository savingAccountRepository;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    private SavingAccountRegisterService savingAccountRegisterService;

    @BeforeEach
    void setUp() {
        savingAccountRegisterService = new SavingAccountRegisterService(
            studentGetByEmailService,
            savingAccountExistsByNameAndWalletService,
            savingAccountRepository,
            transactionGenerateService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu102registerSavingAccount")
    class Cu102registerSavingAccount {

        @Test
        @DisplayName("Given valid request and sufficient wallet balance When registering saving account Then creates account and generates transaction")
        void whenValidRequestAndSufficientBalance_createsAccountAndGeneratesTransaction() {
            // Given - Request válido y wallet con balance suficiente
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            SavingAccountRegisterRequestDto requestDto = InvestmentTestMother
                    .savingAccountRegisterRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                            InvestmentTestMother.AMOUNT_MEDIUM);
            SavingAccount savedAccount = InvestmentTestMother.savingAccount(
                    InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_ID,
                    InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                    requestDto.getInitialAmount(),
                    requestDto.getInitialAmount(),
                    InvestmentTestMother.AMOUNT_ZERO,
                    wallet);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountExistsByNameAndWalletService.execute(requestDto.getName(), wallet)).thenReturn(false);
            when(savingAccountRepository.save(any(SavingAccount.class))).thenReturn(savedAccount);

            // When - Registrar caja de ahorro
            SavingAccountResponseDto result = savingAccountRegisterService.cu102registerSavingAccount(requestDto, user);

            // Then - Debe crear caja de ahorro y generar transacción
            ArgumentCaptor<SavingAccount> accountCaptor = ArgumentCaptor.forClass(SavingAccount.class);
            verify(savingAccountRepository).save(accountCaptor.capture());

            SavingAccount capturedAccount = accountCaptor.getValue();
            assertThat(capturedAccount)
                    .extracting(SavingAccount::getName, SavingAccount::getInitialAmount,
                            SavingAccount::getCurrentAmount, SavingAccount::getWallet)
                    .containsExactly(requestDto.getName(), requestDto.getInitialAmount(),
                            requestDto.getInitialAmount(), wallet);

            verify(transactionGenerateService).generate(eq(TypeTransaction.INGRESO_CAJA_AHORRO),
                    eq(savedAccount.getInitialAmount()), eq("Deposito en caja de ahorro"),
                    eq(TransactionActor.ESTUDIANTE), eq(TransactionActor.SISTEMA), eq(wallet), isNull(), isNull(),
                    isNull(), isNull(), isNull(), eq(savedAccount));
            verify(walletUpdateInvestedBalanceService).execute(wallet);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Given wallet with insufficient balance When registering saving account Then throws BadRequestException")
        void whenInsufficientBalance_throwsBadRequestException() {
            // Given - Request válido pero wallet sin balance suficiente
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.walletWithInsufficientBalance(InvestmentTestMother.DEFAULT_WALLET_ID,
                    InvestmentTestMother.AMOUNT_SMALL, InvestmentTestMother.AMOUNT_MEDIUM);
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            SavingAccountRegisterRequestDto requestDto = InvestmentTestMother
                    .savingAccountRegisterRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                            InvestmentTestMother.AMOUNT_MEDIUM);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> savingAccountRegisterService.cu102registerSavingAccount(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No hay saldo suficiente en la wallet");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(savingAccountRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }

        @Test
        @DisplayName("Given duplicate account name for wallet When registering saving account Then throws BadRequestException")
        void whenDuplicateName_throwsBadRequestException() {
            // Given - Nombre duplicado para el wallet
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            SavingAccountRegisterRequestDto requestDto = InvestmentTestMother
                    .savingAccountRegisterRequestDto(InvestmentTestMother.DEFAULT_SAVING_ACCOUNT_NAME,
                            InvestmentTestMother.AMOUNT_MEDIUM);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountExistsByNameAndWalletService.execute(requestDto.getName(), wallet)).thenReturn(true);

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> savingAccountRegisterService.cu102registerSavingAccount(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Ya existe una cuenta de ahorro con ese nombre");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(savingAccountRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }
    }
}

