package trinity.play2learn.backend.investment.stock.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import trinity.play2learn.backend.investment.stock.dtos.request.StockBuyRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateSpecificService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class StockBuyServiceTest {

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private IStockFindByIdService stockFindByIdService;

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private ITransactionGenerateService transactionGenerateService;

    @Mock
    private IStockMoveService stockMoveService;

    @Mock
    private IStockUpdateSpecificService stockUpdateSpecificService;

    @Mock
    private IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;

    private StockBuyService stockBuyService;

    @BeforeEach
    void setUp() {
        stockBuyService = new StockBuyService(
            studentGetByEmailService,
            stockFindByIdService,
            orderRepository,
            transactionGenerateService,
            stockMoveService,
            stockUpdateSpecificService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu84buystocks")
    class Cu84buystocks {

        @Test
        @DisplayName("Given valid request with sufficient stock availability and wallet balance When buying Then creates order and generates transaction")
        void whenValidRequestAndSufficientAvailability_createsOrderAndGeneratesTransaction() {
            // Given - Request válido con disponibilidad y balance suficientes
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(stock.getId(),
                    InvestmentTestMother.DEFAULT_QUANTITY);
            Double totalPrice = stock.getCurrentPrice() * requestDto.getQuantity().doubleValue();
            Order savedOrder = InvestmentTestMother.orderExecuted(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderType.COMPRA, requestDto.getQuantity());

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockFindByIdService.execute(requestDto.getStockId())).thenReturn(stock);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            // When - Comprar acciones
            try (MockedStatic<OrderMapper> orderMapperMock = mockStatic(OrderMapper.class)) {
                orderMapperMock.when(() -> OrderMapper.toEntity(OrderType.COMPRA, OrderState.EJECUTADA, stock, wallet,
                        requestDto.getQuantity())).thenReturn(savedOrder);
                orderMapperMock.when(() -> OrderMapper.toBuyDto(savedOrder))
                        .thenReturn(InvestmentTestMother.stockBuyResponseDto(savedOrder.getId(),
                                stock.getCurrentPrice(), requestDto.getQuantity(), totalPrice));

                StockBuyResponseDto result = stockBuyService.cu84buystocks(requestDto, user);

                // Then - Debe crear orden y generar transacción
                ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
                verify(orderRepository).save(orderCaptor.capture());

                Order capturedOrder = orderCaptor.getValue();
                assertThat(capturedOrder)
                        .extracting(Order::getOrderType, Order::getOrderState, Order::getQuantity)
                        .containsExactly(OrderType.COMPRA, OrderState.EJECUTADA, requestDto.getQuantity());

                verify(transactionGenerateService).generate(eq(TypeTransaction.STOCK), eq(totalPrice),
                        eq("Compra de acciones"), eq(TransactionActor.ESTUDIANTE), eq(TransactionActor.SISTEMA),
                        eq(wallet), eq(null), eq(null), eq(null), eq(savedOrder), eq(null), eq(null));
                verify(stockMoveService).toSold(stock, requestDto.getQuantity());
                verify(stockUpdateSpecificService).execute(stock);
                verify(walletUpdateInvestedBalanceService).execute(wallet);
                assertThat(result).isNotNull();
            }
        }

        @Test
        @DisplayName("Given insufficient stock availability When buying Then throws BadRequestException")
        void whenInsufficientAvailability_throwsBadRequestException() {
            // Given - Acciones no disponibles suficientes
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            Stock stock = InvestmentTestMother.stockWithAvailability(InvestmentTestMother.DEFAULT_STOCK_ID,
                    BigInteger.valueOf(5), InvestmentTestMother.DEFAULT_SOLD_AMOUNT);
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(stock.getId(),
                    InvestmentTestMother.DEFAULT_QUANTITY);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockFindByIdService.execute(requestDto.getStockId())).thenReturn(stock);

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> stockBuyService.cu84buystocks(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No hay acciones disponibles suficientes para comprar.");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(orderRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(stockMoveService);
            verifyNoInteractions(stockUpdateSpecificService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }

        @Test
        @DisplayName("Given insufficient wallet balance When buying Then throws BadRequestException")
        void whenInsufficientBalance_throwsBadRequestException() {
            // Given - Wallet sin balance suficiente
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.walletWithInsufficientBalance(InvestmentTestMother.DEFAULT_WALLET_ID,
                    InvestmentTestMother.AMOUNT_SMALL, InvestmentTestMother.AMOUNT_LARGE);
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(stock.getId(),
                    InvestmentTestMother.DEFAULT_QUANTITY);

            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockFindByIdService.execute(requestDto.getStockId())).thenReturn(stock);

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> stockBuyService.cu84buystocks(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No hay saldo suficiente en el wallet para realizar la compra.");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(orderRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(stockMoveService);
            verifyNoInteractions(stockUpdateSpecificService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }
    }
}

