package trinity.play2learn.backend.investment.stock.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.services.interfaces.IOrderFindPendingService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class StockGetServiceTest {

    @Mock
    private IStockFindByIdService stockFindByIdService;

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private IStockCalculateByWalletService stockCalculateByWalletService;

    @Mock
    private IOrderFindPendingService orderFindPendingService;

    private StockGetService stockGetService;

    @BeforeEach
    void setUp() {
        stockGetService = new StockGetService(
            stockFindByIdService,
            studentGetByEmailService,
            stockCalculateByWalletService,
            orderFindPendingService
        );
    }

    @Nested
    @DisplayName("cu100GetStock")
    class Cu100GetStock {

        @Test
        @DisplayName("Given valid stock id and user When getting stock Then returns stock with quantity and pending orders")
        void whenValidStockId_returnsStockWithQuantityAndPendingOrders() {
            // Given - ID válido y usuario
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            BigInteger quantityBought = InvestmentTestMother.DEFAULT_QUANTITY;
            List<StockSellResponseDto> pendingOrders = List.of(
                    InvestmentTestMother.stockSellResponseDto(InvestmentTestMother.DEFAULT_QUANTITY,
                            InvestmentTestMother.DEFAULT_CURRENT_PRICE, 1100.0));

            when(stockFindByIdService.execute(stock.getId())).thenReturn(stock);
            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(quantityBought);
            when(orderFindPendingService.execute(stock, wallet)).thenReturn(pendingOrders);

            // When - Obtener acción
            try (MockedStatic<StockMapper> stockMapperMock = mockStatic(StockMapper.class)) {
                StockResponseDto expectedDto = InvestmentTestMother.stockResponseDto(stock.getId(),
                        stock.getName(), stock.getAbbreviation(), stock.getCurrentPrice(), quantityBought,
                        pendingOrders);
                stockMapperMock.when(() -> StockMapper.toDto(stock, quantityBought, pendingOrders))
                        .thenReturn(expectedDto);

                StockResponseDto result = stockGetService.cu100GetStock(stock.getId(), user);

                // Then - Debe retornar acción con cantidad y órdenes pendientes
                verify(stockFindByIdService).execute(stock.getId());
                verify(studentGetByEmailService).getByEmail(user.getEmail());
                verify(stockCalculateByWalletService).execute(stock, wallet);
                verify(orderFindPendingService).execute(stock, wallet);
                stockMapperMock.verify(() -> StockMapper.toDto(stock, quantityBought, pendingOrders));
                assertThat(result).isEqualTo(expectedDto);
            }
        }

        @Test
        @DisplayName("Given stock with no pending orders When getting stock Then returns stock with empty pending orders")
        void whenNoPendingOrders_returnsStockWithEmptyPendingOrders() {
            // Given - Acción sin órdenes pendientes
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            BigInteger quantityBought = BigInteger.ZERO;

            when(stockFindByIdService.execute(stock.getId())).thenReturn(stock);
            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(quantityBought);
            when(orderFindPendingService.execute(stock, wallet)).thenReturn(List.of());

            // When - Obtener acción
            try (MockedStatic<StockMapper> stockMapperMock = mockStatic(StockMapper.class)) {
                StockResponseDto expectedDto = InvestmentTestMother.stockResponseDto(stock.getId(),
                        stock.getName(), stock.getAbbreviation(), stock.getCurrentPrice(), quantityBought, List.of());
                stockMapperMock.when(() -> StockMapper.toDto(stock, quantityBought, List.of()))
                        .thenReturn(expectedDto);

                StockResponseDto result = stockGetService.cu100GetStock(stock.getId(), user);

                // Then - Debe retornar acción con lista vacía de órdenes pendientes
                assertThat(result.getPendingOrders()).isEmpty();
                assertThat(result.getQuantityBought()).isZero();
            }
        }
    }
}

