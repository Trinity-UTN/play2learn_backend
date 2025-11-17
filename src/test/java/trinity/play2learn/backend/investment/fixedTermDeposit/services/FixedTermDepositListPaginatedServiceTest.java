package trinity.play2learn.backend.investment.fixedTermDeposit.services;

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
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.mappers.FixedTermDepositMapper;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.specs.FixedTermDepositSpecs;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class FixedTermDepositListPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "startDate";
    private static final String ORDER_TYPE = "desc";

    @Mock
    private IFixedTermDepositRepository fixedTermDepositRepository;

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    private FixedTermDepositListPaginatedService fixedTermDepositListPaginatedService;

    @BeforeEach
    void setUp() {
        fixedTermDepositListPaginatedService = new FixedTermDepositListPaginatedService(
            fixedTermDepositRepository,
            studentGetByEmailService
        );
    }

    @Nested
    @DisplayName("cu99ListPaginatedFixedTermDeposits")
    class Cu99ListPaginatedFixedTermDeposits {

        @Test
        @DisplayName("Given valid pagination parameters When listing Then returns paginated fixed term deposits")
        void whenValidParameters_returnsPaginatedData() {
            // Given - Parámetros de paginación válidos
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            FixedTermDeposit deposit = InvestmentTestMother.defaultFixedTermDeposit();
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<FixedTermDeposit> pageResult = new PageImpl<>(List.of(deposit), pageable, 1);

            FixedTermDepositResponseDto depositDto = InvestmentTestMother.fixedTermDepositResponseDto(
                    deposit.getId(), deposit.getAmountInvested(), deposit.getAmountReward(),
                    deposit.getFixedTermDays(), deposit.getFixedTermState());
            PaginatedData<FixedTermDepositResponseDto> expectedPaginatedData = PaginatedData
                    .<FixedTermDepositResponseDto>builder()
                    .results(List.of(depositDto))
                    .count(1)
                    .totalPages(1)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(fixedTermDepositRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(pageResult);

            // When - Listar plazos fijos paginados
            try (
                    MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                    MockedStatic<FixedTermDepositSpecs> specsMock = mockStatic(FixedTermDepositSpecs.class);
                    MockedStatic<FixedTermDepositMapper> mapperMock = mockStatic(FixedTermDepositMapper.class);
                    MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                Specification<FixedTermDeposit> walletSpec = (root, query, cb) -> null;
                specsMock.when(() -> FixedTermDepositSpecs.hasWallet(wallet)).thenReturn(walletSpec);

                mapperMock.when(() -> FixedTermDepositMapper.toDtoList(pageResult.getContent()))
                        .thenReturn(List.of(depositDto));
                paginationHelperMock.when(() -> PaginationHelper.fromPage(pageResult, List.of(depositDto)))
                        .thenReturn(expectedPaginatedData);

                PaginatedData<FixedTermDepositResponseDto> result = fixedTermDepositListPaginatedService
                        .cu99ListPaginatedFixedTermDeposits(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                // Then - Debe retornar datos paginados
                paginatorUtilsMock.verify(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE));
                specsMock.verify(() -> FixedTermDepositSpecs.hasWallet(wallet));
                mapperMock.verify(() -> FixedTermDepositMapper.toDtoList(pageResult.getContent()));
                paginationHelperMock.verify(() -> PaginationHelper.fromPage(pageResult, List.of(depositDto)));
                verify(fixedTermDepositRepository).findAll(any(Specification.class), eq(pageable));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }

        @Test
        @DisplayName("Given filters for state and days When listing Then applies all filters correctly")
        void whenFiltersProvided_appliesAllFilters() {
            // Given - Filtros por estado y días
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            List<String> filters = List.of("fixedTermState", "fixedTermDays");
            List<String> filterValues = List.of("IN_PROGRESS", "MENSUAL");
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<FixedTermDeposit> pageResult = new PageImpl<>(List.of(), pageable, 0);
            PaginatedData<FixedTermDepositResponseDto> expectedPaginatedData = PaginatedData
                    .<FixedTermDepositResponseDto>builder()
                    .results(List.of())
                    .count(0)
                    .totalPages(0)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(fixedTermDepositRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(pageResult);

            // When - Listar con filtros
            try (
                    MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                    MockedStatic<FixedTermDepositSpecs> specsMock = mockStatic(FixedTermDepositSpecs.class);
                    MockedStatic<FixedTermDepositMapper> mapperMock = mockStatic(FixedTermDepositMapper.class);
                    MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                Specification<FixedTermDeposit> walletSpec = (root, query, cb) -> null;
                Specification<FixedTermDeposit> stateSpec = (root, query, cb) -> null;
                Specification<FixedTermDeposit> daysSpec = (root, query, cb) -> null;

                specsMock.when(() -> FixedTermDepositSpecs.hasWallet(wallet)).thenReturn(walletSpec);
                specsMock.when(() -> FixedTermDepositSpecs.hasState("IN_PROGRESS")).thenReturn(stateSpec);
                specsMock.when(() -> FixedTermDepositSpecs.hasDays("MENSUAL")).thenReturn(daysSpec);

                mapperMock.when(() -> FixedTermDepositMapper.toDtoList(pageResult.getContent()))
                        .thenReturn(List.of());
                paginationHelperMock.when(() -> PaginationHelper.fromPage(pageResult, List.of()))
                        .thenReturn(expectedPaginatedData);

                PaginatedData<FixedTermDepositResponseDto> result = fixedTermDepositListPaginatedService
                        .cu99ListPaginatedFixedTermDeposits(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, filters,
                                filterValues, user);

                // Then - Debe aplicar todos los filtros
                specsMock.verify(() -> FixedTermDepositSpecs.hasWallet(wallet));
                specsMock.verify(() -> FixedTermDepositSpecs.hasState("IN_PROGRESS"));
                specsMock.verify(() -> FixedTermDepositSpecs.hasDays("MENSUAL"));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }

        @Test
        @DisplayName("Given empty results When listing Then returns empty paginated data")
        void whenEmptyResults_returnsEmptyPaginatedData() {
            // Given - Sin resultados
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<FixedTermDeposit> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedData<FixedTermDepositResponseDto> expectedPaginatedData = PaginatedData
                    .<FixedTermDepositResponseDto>builder()
                    .results(List.of())
                    .count(0)
                    .totalPages(0)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(fixedTermDepositRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

            // When - Listar sin resultados
            try (
                    MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                    MockedStatic<FixedTermDepositSpecs> specsMock = mockStatic(FixedTermDepositSpecs.class);
                    MockedStatic<FixedTermDepositMapper> mapperMock = mockStatic(FixedTermDepositMapper.class);
                    MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                Specification<FixedTermDeposit> walletSpec = (root, query, cb) -> null;
                specsMock.when(() -> FixedTermDepositSpecs.hasWallet(wallet)).thenReturn(walletSpec);

                mapperMock.when(() -> FixedTermDepositMapper.toDtoList(emptyPage.getContent()))
                        .thenReturn(List.of());
                paginationHelperMock.when(() -> PaginationHelper.fromPage(emptyPage, List.of()))
                        .thenReturn(expectedPaginatedData);

                PaginatedData<FixedTermDepositResponseDto> result = fixedTermDepositListPaginatedService
                        .cu99ListPaginatedFixedTermDeposits(PAGE, SIZE, ORDER_BY, ORDER_TYPE, null, null, null, user);

                // Then - Debe retornar datos vacíos
                assertThat(result.getResults()).isEmpty();
                assertThat(result.getCount()).isZero();
            }
        }
    }
}

