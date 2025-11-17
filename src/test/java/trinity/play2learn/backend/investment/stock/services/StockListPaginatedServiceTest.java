package trinity.play2learn.backend.investment.stock.services;

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

import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.specs.StockSpecs;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@ExtendWith(MockitoExtension.class)
class StockListPaginatedServiceTest {

    private static final int PAGE = 1;
    private static final int SIZE = 10;
    private static final String ORDER_BY = "name";
    private static final String ORDER_TYPE = "asc";

    @Mock
    private IStockRepository stockRepository;

    private StockListPaginatedService stockListPaginatedService;

    @BeforeEach
    void setUp() {
        stockListPaginatedService = new StockListPaginatedService(stockRepository);
    }

    @Nested
    @DisplayName("cu87ListPaginatedStock")
    class Cu87ListPaginatedStock {

        @Test
        @DisplayName("Given valid pagination parameters with search When listing Then returns paginated stocks")
        void whenValidParametersWithSearch_returnsPaginatedData() {
            // Given - Parámetros de paginación válidos con búsqueda
            Stock stock = InvestmentTestMother.defaultStock();
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<Stock> pageResult = new PageImpl<>(List.of(stock), pageable, 1);
            String search = "Ejemplo";

            StockResponseDto stockDto = InvestmentTestMother.stockResponseDto(stock.getId(), stock.getName(),
                    stock.getAbbreviation(), stock.getCurrentPrice(), null, null);
            PaginatedData<StockResponseDto> expectedPaginatedData = PaginatedData.<StockResponseDto>builder()
                    .results(List.of(stockDto))
                    .count(1)
                    .totalPages(1)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();

            when(stockRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

            // When - Listar acciones paginadas
            try (
                    MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                    MockedStatic<StockSpecs> specsMock = mockStatic(StockSpecs.class);
                    MockedStatic<StockMapper> mapperMock = mockStatic(StockMapper.class);
                    MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                Specification<Stock> nameSpec = (root, query, cb) -> null;
                specsMock.when(() -> StockSpecs.nameContains(search)).thenReturn(nameSpec);

                mapperMock.when(() -> StockMapper.toDtoList(pageResult.getContent())).thenReturn(List.of(stockDto));
                paginationHelperMock.when(() -> PaginationHelper.fromPage(pageResult, List.of(stockDto)))
                        .thenReturn(expectedPaginatedData);

                PaginatedData<StockResponseDto> result = stockListPaginatedService.cu87ListPaginatedStock(PAGE, SIZE,
                        ORDER_BY, ORDER_TYPE, search, null, null);

                // Then - Debe retornar datos paginados con búsqueda
                paginatorUtilsMock.verify(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE));
                specsMock.verify(() -> StockSpecs.nameContains(search));
                mapperMock.verify(() -> StockMapper.toDtoList(pageResult.getContent()));
                paginationHelperMock.verify(() -> PaginationHelper.fromPage(pageResult, List.of(stockDto)));
                verify(stockRepository).findAll(any(Specification.class), eq(pageable));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }

        @Test
        @DisplayName("Given filters When listing Then applies all filters correctly")
        void whenFiltersProvided_appliesAllFilters() {
            // Given - Filtros genéricos
            List<String> filters = List.of("riskLevel", "currentPrice");
            List<String> filterValues = List.of("MEDIO", "100");
            Pageable pageable = Pageable.ofSize(SIZE);
            Page<Stock> pageResult = new PageImpl<>(List.of(), pageable, 0);
            PaginatedData<StockResponseDto> expectedPaginatedData = PaginatedData.<StockResponseDto>builder()
                    .results(List.of())
                    .count(0)
                    .totalPages(0)
                    .currentPage(PAGE)
                    .pageSize(SIZE)
                    .build();

            when(stockRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

            // When - Listar con filtros
            try (
                    MockedStatic<PaginatorUtils> paginatorUtilsMock = mockStatic(PaginatorUtils.class);
                    MockedStatic<StockSpecs> specsMock = mockStatic(StockSpecs.class);
                    MockedStatic<StockMapper> mapperMock = mockStatic(StockMapper.class);
                    MockedStatic<PaginationHelper> paginationHelperMock = mockStatic(PaginationHelper.class)) {
                paginatorUtilsMock.when(() -> PaginatorUtils.buildPageable(PAGE, SIZE, ORDER_BY, ORDER_TYPE))
                        .thenReturn(pageable);

                Specification<Stock> riskSpec = (root, query, cb) -> null;
                Specification<Stock> priceSpec = (root, query, cb) -> null;

                specsMock.when(() -> StockSpecs.genericFilter("riskLevel", "MEDIO")).thenReturn(riskSpec);
                specsMock.when(() -> StockSpecs.genericFilter("currentPrice", "100")).thenReturn(priceSpec);

                mapperMock.when(() -> StockMapper.toDtoList(pageResult.getContent())).thenReturn(List.of());
                paginationHelperMock.when(() -> PaginationHelper.fromPage(pageResult, List.of()))
                        .thenReturn(expectedPaginatedData);

                PaginatedData<StockResponseDto> result = stockListPaginatedService.cu87ListPaginatedStock(PAGE, SIZE,
                        ORDER_BY, ORDER_TYPE, null, filters, filterValues);

                // Then - Debe aplicar todos los filtros
                specsMock.verify(() -> StockSpecs.genericFilter("riskLevel", "MEDIO"));
                specsMock.verify(() -> StockSpecs.genericFilter("currentPrice", "100"));
                assertThat(result).isEqualTo(expectedPaginatedData);
            }
        }
    }
}

