package trinity.play2learn.backend.investment.savingAccount.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.mappers.SavingAccountMapper;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.specs.SavingAccountSpecs;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class SavingAccountListPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "name";
    private static final String ORDER_TYPE = "asc";

    @Mock
    private ISavingAccountRepository savingAccountRepository;

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    private SavingAccountListPaginatedService savingAccountListPaginatedService;

    @BeforeEach
    void setUp() {
        savingAccountListPaginatedService = new SavingAccountListPaginatedService(
            savingAccountRepository,
            studentGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu106listPaginatedSavingAccounts")
    class Cu106listPaginatedSavingAccounts {

        @Test
        @DisplayName("Given valid pagination parameters When listing Then returns paginated saving accounts")
        void whenValidParameters_returnsPaginatedData() {
            // Given - Parámetros de paginación válidos
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            SavingAccount account = InvestmentTestMother.defaultSavingAccount();
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<SavingAccount> pageResult = new PageImpl<>(List.of(account), pageable, 1);

            SavingAccountResponseDto accountDto = InvestmentTestMother.savingAccountResponseDto(account.getId(),
                    account.getName(), account.getInitialAmount(), account.getCurrentAmount(),
                    account.getAccumulatedInterest());
            PaginatedData<SavingAccountResponseDto> expectedPaginatedData = PaginatedData
                    .<SavingAccountResponseDto>builder()
                    .results(List.of(accountDto))
                    .count(1)
                    .totalPages(1)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

            // When - Listar cajas de ahorro paginadas
            try (
                    MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                    MockedStatic<SavingAccountSpecs> specsMock = mockStatic(SavingAccountSpecs.class);
                    MockedStatic<SavingAccountMapper> mapperMock = mockStatic(SavingAccountMapper.class);
                    MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                Specification<SavingAccount> notDeletedSpec = (root, query, cb) -> null;
                Specification<SavingAccount> walletSpec = (root, query, cb) -> null;
                specsMock.when(() -> SavingAccountSpecs.notDeleted()).thenReturn(notDeletedSpec);
                specsMock.when(() -> SavingAccountSpecs.hasWallet(wallet)).thenReturn(walletSpec);

                mapperMock.when(() -> SavingAccountMapper.toDtoList(pageResult.getContent()))
                        .thenReturn(List.of(accountDto));
                paginationHelperMock.when(() -> PaginationHelper.fromPage(pageResult, List.of(accountDto)))
                        .thenReturn(expectedPaginatedData);

                PaginatedData<SavingAccountResponseDto> result = savingAccountListPaginatedService
                        .cu106listPaginatedSavingAccounts(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                // Then - Debe retornar datos paginados
                paginatorUtilsMock.verify(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE));
                specsMock.verify(() -> SavingAccountSpecs.notDeleted());
                specsMock.verify(() -> SavingAccountSpecs.hasWallet(wallet));
                mapperMock.verify(() -> SavingAccountMapper.toDtoList(pageResult.getContent()));
                paginationHelperMock.verify(() -> PaginationHelper.fromPage(pageResult, List.of(accountDto)));
                verify(savingAccountRepository).findAll(any(Specification.class), eq(pageable));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }

        @Test
        @DisplayName("Given filters When listing Then applies all filters correctly")
        void whenFiltersProvided_appliesAllFilters() {
            // Given - Filtros genéricos
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            List<String> filters = List.of("name", "currentAmount");
            List<String> filterValues = List.of("Principal", "5000");
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<SavingAccount> pageResult = new PageImpl<>(List.of(), pageable, 0);
            PaginatedData<SavingAccountResponseDto> expectedPaginatedData = PaginatedData
                    .<SavingAccountResponseDto>builder()
                    .results(List.of())
                    .count(0)
                    .totalPages(0)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(savingAccountRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

            // When - Listar con filtros
            try (
                    MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                    MockedStatic<SavingAccountSpecs> specsMock = mockStatic(SavingAccountSpecs.class);
                    MockedStatic<SavingAccountMapper> mapperMock = mockStatic(SavingAccountMapper.class);
                    MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                Specification<SavingAccount> notDeletedSpec = (root, query, cb) -> null;
                Specification<SavingAccount> walletSpec = (root, query, cb) -> null;
                Specification<SavingAccount> nameSpec = (root, query, cb) -> null;
                Specification<SavingAccount> amountSpec = (root, query, cb) -> null;

                specsMock.when(() -> SavingAccountSpecs.notDeleted()).thenReturn(notDeletedSpec);
                specsMock.when(() -> SavingAccountSpecs.hasWallet(wallet)).thenReturn(walletSpec);
                specsMock.when(() -> SavingAccountSpecs.genericFilter("name", "Principal")).thenReturn(nameSpec);
                specsMock.when(() -> SavingAccountSpecs.genericFilter("currentAmount", "5000")).thenReturn(amountSpec);

                mapperMock.when(() -> SavingAccountMapper.toDtoList(pageResult.getContent())).thenReturn(List.of());
                paginationHelperMock.when(() -> PaginationHelper.fromPage(pageResult, List.of()))
                        .thenReturn(expectedPaginatedData);

                PaginatedData<SavingAccountResponseDto> result = savingAccountListPaginatedService
                        .cu106listPaginatedSavingAccounts(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, filters, filterValues,
                                user);

                // Then - Debe aplicar todos los filtros
                specsMock.verify(() -> SavingAccountSpecs.genericFilter("name", "Principal"));
                specsMock.verify(() -> SavingAccountSpecs.genericFilter("currentAmount", "5000"));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }
    }
}

