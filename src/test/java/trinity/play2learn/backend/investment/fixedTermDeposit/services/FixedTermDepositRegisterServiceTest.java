package trinity.play2learn.backend.investment.fixedTermDeposit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

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
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositCalculateInterestService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class FixedTermDepositRegisterServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private IFixedTermDepositCalculateInterestService fixedTermDepositCalculateInterestService;

    @Mock
    private IFixedTermDepositRepository fixedTermDepositRepository;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    private FixedTermDepositRegisterService fixedTermDepositRegisterService;

    @BeforeEach
    void setUp() {
        fixedTermDepositRegisterService = new FixedTermDepositRegisterService(
            studentGetByEmailService,
            fixedTermDepositCalculateInterestService,
            fixedTermDepositRepository,
            transactionGenerateService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu92registerFixedTermDeposit")
    class Cu92registerFixedTermDeposit {

        @Test
        @DisplayName("Given valid request and sufficient wallet balance When registering fixed term deposit Then creates deposit and generates transaction")
        void whenValidRequestAndSufficientBalance_createsDepositAndGeneratesTransaction() {
            // Given - Request válido y wallet con balance suficiente
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            FixedTermDepositRegisterRequestDto requestDto = InvestmentTestMother
                    .fixedTermDepositRegisterRequestDto(InvestmentTestMother.AMOUNT_MEDIUM, FixedTermDays.MENSUAL);
            Double interest = InvestmentTestMother.INTEREST_MEDIUM;
            Double expectedAmountReward = requestDto.getAmountInvested() + interest;
            FixedTermDeposit savedDeposit = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, requestDto.getAmountInvested(),
                    expectedAmountReward, requestDto.getFixedTermDays(), FixedTermState.IN_PROGRESS, wallet);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(fixedTermDepositCalculateInterestService.execute(eq(requestDto.getAmountInvested()),
                    eq(requestDto.getFixedTermDays()))).thenReturn(interest);
            when(fixedTermDepositRepository.save(any(FixedTermDeposit.class))).thenReturn(savedDeposit);

            // When - Registrar plazo fijo
            FixedTermDepositResponseDto result = fixedTermDepositRegisterService
                    .cu92registerFixedTermDeposit(requestDto, user);

            // Then - Debe crear plazo fijo y generar transacción
            ArgumentCaptor<FixedTermDeposit> depositCaptor = ArgumentCaptor.forClass(FixedTermDeposit.class);
            verify(fixedTermDepositRepository).save(depositCaptor.capture());

            FixedTermDeposit capturedDeposit = depositCaptor.getValue();
            assertThat(capturedDeposit)
                    .extracting(FixedTermDeposit::getAmountInvested, FixedTermDeposit::getAmountReward,
                            FixedTermDeposit::getFixedTermDays, FixedTermDeposit::getFixedTermState,
                            FixedTermDeposit::getWallet)
                    .containsExactly(requestDto.getAmountInvested(), expectedAmountReward,
                            requestDto.getFixedTermDays(), FixedTermState.IN_PROGRESS, wallet);

            verify(transactionGenerateService).generate(eq(TypeTransaction.PLAZO_FIJO),
                    eq(requestDto.getAmountInvested()), eq("Inversion en plazo fijo."),
                    eq(TransactionActor.ESTUDIANTE), eq(TransactionActor.SISTEMA), eq(wallet), eq(null), eq(null),
                    eq(null), eq(null), any(FixedTermDeposit.class), eq(null));
            verify(walletUpdateInvestedBalanceService).execute(wallet);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Given wallet with insufficient balance When registering fixed term deposit Then throws UnsupportedOperationException")
        void whenInsufficientBalance_throwsUnsupportedOperationException() {
            // Given - Request válido pero wallet sin balance suficiente
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.walletWithInsufficientBalance(InvestmentTestMother.DEFAULT_WALLET_ID,
                    InvestmentTestMother.AMOUNT_SMALL, InvestmentTestMother.AMOUNT_MEDIUM);
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            FixedTermDepositRegisterRequestDto requestDto = InvestmentTestMother
                    .fixedTermDepositRegisterRequestDto(InvestmentTestMother.AMOUNT_MEDIUM, FixedTermDays.MENSUAL);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);

            // When & Then - Debe lanzar UnsupportedOperationException
            assertThatThrownBy(() -> fixedTermDepositRegisterService.cu92registerFixedTermDeposit(requestDto, user))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("No hay saldo suficiente en la wallet");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(fixedTermDepositCalculateInterestService);
            verifyNoInteractions(fixedTermDepositRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }

        @Test
        @DisplayName("Given valid request with different fixed term days When registering fixed term deposit Then calculates correct end date")
        void whenDifferentFixedTermDays_calculatesCorrectEndDate() {
            // Given - Request con diferentes plazos
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            FixedTermDepositRegisterRequestDto requestDto = InvestmentTestMother
                    .fixedTermDepositRegisterRequestDto(InvestmentTestMother.AMOUNT_MEDIUM, FixedTermDays.SEMANAL);
            Double interest = InvestmentTestMother.INTEREST_SMALL;
            FixedTermDeposit savedDeposit = InvestmentTestMother.fixedTermDeposit(
                    InvestmentTestMother.DEFAULT_FIXED_TERM_DEPOSIT_ID, requestDto.getAmountInvested(),
                    requestDto.getAmountInvested() + interest, requestDto.getFixedTermDays(),
                    FixedTermState.IN_PROGRESS, wallet);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(fixedTermDepositCalculateInterestService.execute(eq(requestDto.getAmountInvested()),
                    eq(requestDto.getFixedTermDays()))).thenReturn(interest);
            when(fixedTermDepositRepository.save(any(FixedTermDeposit.class))).thenReturn(savedDeposit);

            // When - Registrar plazo fijo semanal
            fixedTermDepositRegisterService.cu92registerFixedTermDeposit(requestDto, user);

            // Then - Debe calcular fecha de finalización correcta (7 días)
            ArgumentCaptor<FixedTermDeposit> depositCaptor = ArgumentCaptor.forClass(FixedTermDeposit.class);
            verify(fixedTermDepositRepository).save(depositCaptor.capture());

            FixedTermDeposit capturedDeposit = depositCaptor.getValue();
            LocalDate expectedEndDate = LocalDate.now().plusDays(FixedTermDays.SEMANAL.getValor());
            assertThat(capturedDeposit.getEndDate()).isEqualTo(expectedEndDate);
        }
    }
}

