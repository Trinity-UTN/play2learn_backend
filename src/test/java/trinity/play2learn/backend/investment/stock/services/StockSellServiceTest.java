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
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateSpecificService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class StockSellServiceTest {

    @Mock
    private IStockFindByIdService stockFindByIdService;

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private IStockCalculateByWalletService stockCalculateByWalletService;

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

    private StockSellService stockSellService;

    @BeforeEach
    void setUp() {
        stockSellService = new StockSellService(
            stockFindByIdService,
            studentGetByEmailService,
            stockCalculateByWalletService,
            orderRepository,
            transactionGenerateService,
            stockMoveService,
            stockUpdateSpecificService,
            walletUpdateInvestedBalanceService
        );
    }

    @Nested
    @DisplayName("cu90sellStock")
    class Cu90sellStock {

        @Test
        @DisplayName("Given valid request with sufficient stock quantity in wallet When selling Then creates order and generates transaction")
        void whenValidRequestAndSufficientQuantity_createsOrderAndGeneratesTransaction() {
            // Given - Request válido con cantidad suficiente en wallet
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(stock.getId(),
                    InvestmentTestMother.DEFAULT_QUANTITY);
            Double totalPrice = stock.getCurrentPrice() * requestDto.getQuantity().doubleValue();
            Order savedOrder = InvestmentTestMother.orderExecuted(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderType.VENTA, requestDto.getQuantity());

            when(stockFindByIdService.execute(requestDto.getStockId())).thenReturn(stock);
            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(InvestmentTestMother.DEFAULT_QUANTITY);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            // When - Vender acciones
            try (MockedStatic<OrderMapper> orderMapperMock = mockStatic(OrderMapper.class)) {
                orderMapperMock.when(() -> OrderMapper.toEntity(OrderType.VENTA, OrderState.EJECUTADA, stock, wallet,
                        requestDto.getQuantity())).thenReturn(savedOrder);
                orderMapperMock.when(() -> OrderMapper.toSellDto(savedOrder))
                        .thenReturn(InvestmentTestMother.stockSellResponseDto(requestDto.getQuantity(),
                                stock.getCurrentPrice(), totalPrice));

                StockSellResponseDto result = stockSellService.cu90sellStock(requestDto, user);

                // Then - Debe crear orden y generar transacción
                ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
                verify(orderRepository).save(orderCaptor.capture());

                Order capturedOrder = orderCaptor.getValue();
                assertThat(capturedOrder.getOrderType()).isEqualTo(OrderType.VENTA);
                assertThat(capturedOrder.getOrderState()).isEqualTo(OrderState.EJECUTADA);
                assertThat(capturedOrder.getQuantity()).isEqualTo(requestDto.getQuantity());

                verify(transactionGenerateService).generate(eq(TypeTransaction.STOCK), eq(totalPrice),
                        eq("Venta de acciones"), eq(TransactionActor.SISTEMA), eq(TransactionActor.ESTUDIANTE),
                        eq(wallet), eq(null), eq(null), eq(null), eq(savedOrder), eq(null), eq(null));
                verify(stockMoveService).toAvailable(stock, requestDto.getQuantity());
                verify(stockUpdateSpecificService).execute(stock);
                verify(walletUpdateInvestedBalanceService).execute(wallet);
                assertThat(result).isNotNull();
            }
        }

        @Test
        @DisplayName("Given insufficient stock quantity in wallet When selling Then throws BadRequestException")
        void whenInsufficientQuantity_throwsBadRequestException() {
            // Given - Cantidad insuficiente en wallet
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            StockBuyRequestDto requestDto = InvestmentTestMother.stockBuyRequestDto(stock.getId(),
                    InvestmentTestMother.DEFAULT_QUANTITY);

            when(stockFindByIdService.execute(requestDto.getStockId())).thenReturn(stock);
            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(BigInteger.valueOf(5));

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> stockSellService.cu90sellStock(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El wallet no cuenta con las acciones suficientes para realizar la venta");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(orderRepository);
            verifyNoInteractions(transactionGenerateService);
            verifyNoInteractions(stockMoveService);
            verifyNoInteractions(stockUpdateSpecificService);
            verifyNoInteractions(walletUpdateInvestedBalanceService);
        }
    }
}

