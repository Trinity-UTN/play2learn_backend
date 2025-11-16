package trinity.play2learn.backend.investment.stock.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.request.StockOrderStopRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderStop;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.user.models.User;

@ExtendWith(MockitoExtension.class)
class StockRegisterStopServiceTest {

    @Mock
    private IStockFindByIdService stockFindByIdService;

    @Mock
    private IStudentGetByEmailService studentGetByEmailService;

    @Mock
    private IStockCalculateByWalletService stockCalculateByWalletService;

    @Mock
    private IOrderRepository orderRepository;

    private StockRegisterStopService stockRegisterStopService;

    @BeforeEach
    void setUp() {
        stockRegisterStopService = new StockRegisterStopService(
            stockFindByIdService,
            studentGetByEmailService,
            stockCalculateByWalletService,
            orderRepository
        );
    }

    @Nested
    @DisplayName("cu91registerStopStock")
    class Cu91registerStopStock {

        @Test
        @DisplayName("Given valid request with sufficient stock quantity in wallet When registering stop Then creates pending order")
        void whenValidRequestAndSufficientQuantity_createsPendingOrder() {
            // Given - Request válido con cantidad suficiente en wallet
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID,
                    user.getEmail(), wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            StockOrderStopRequestDto requestDto = InvestmentTestMother.stockOrderStopRequestDto(stock.getId(),
                    InvestmentTestMother.DEFAULT_QUANTITY, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    OrderStop.LOSS);
            Order savedOrder = InvestmentTestMother.orderPending(InvestmentTestMother.DEFAULT_ORDER_ID,
                    OrderStop.LOSS, requestDto.getPricePerUnit());

            when(stockFindByIdService.execute(requestDto.getStockId())).thenReturn(stock);
            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(InvestmentTestMother.DEFAULT_QUANTITY);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            // When - Registrar orden stop
            try (MockedStatic<OrderMapper> orderMapperMock = mockStatic(OrderMapper.class)) {
                orderMapperMock.when(() -> OrderMapper.toStopEntity(OrderType.VENTA, OrderState.PENDIENTE, stock,
                        wallet, requestDto.getQuantity(), requestDto.getPricePerUnit(), requestDto.getOrderStop()))
                        .thenReturn(savedOrder);
                orderMapperMock.when(() -> OrderMapper.toBuyDto(savedOrder))
                        .thenReturn(InvestmentTestMother.stockBuyResponseDto(savedOrder.getId(),
                                requestDto.getPricePerUnit(), requestDto.getQuantity(),
                                requestDto.getPricePerUnit() * requestDto.getQuantity().doubleValue()));

                StockBuyResponseDto result = stockRegisterStopService.cu91registerStopStock(requestDto, user);

                // Then - Debe crear orden pendiente
                ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
                verify(orderRepository).save(orderCaptor.capture());

                Order capturedOrder = orderCaptor.getValue();
                assertThat(capturedOrder)
                        .extracting(Order::getOrderType, Order::getOrderState, Order::getOrderStop,
                                Order::getQuantity, Order::getPricePerUnit)
                        .containsExactly(OrderType.VENTA, OrderState.PENDIENTE, OrderStop.LOSS,
                                requestDto.getQuantity(), requestDto.getPricePerUnit());
                assertThat(result).isNotNull();
            }
        }

        @Test
        @DisplayName("Given insufficient stock quantity in wallet When registering stop Then throws BadRequestException")
        void whenInsufficientQuantity_throwsBadRequestException() {
            // Given - Cantidad insuficiente en wallet
            User user = InvestmentTestMother.defaultUser();
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Student student = InvestmentTestMother.student(InvestmentTestMother.DEFAULT_STUDENT_ID, user.getEmail(),
                    wallet);
            Stock stock = InvestmentTestMother.defaultStock();
            StockOrderStopRequestDto requestDto = InvestmentTestMother.stockOrderStopRequestDto(stock.getId(),
                    InvestmentTestMother.DEFAULT_QUANTITY, InvestmentTestMother.DEFAULT_CURRENT_PRICE,
                    OrderStop.PROFIT);

            when(stockFindByIdService.execute(requestDto.getStockId())).thenReturn(stock);
            when(studentGetByEmailService.getByEmail(user.getEmail())).thenReturn(student);
            when(stockCalculateByWalletService.execute(stock, wallet)).thenReturn(BigInteger.valueOf(5));

            // When & Then - Debe lanzar BadRequestException
            assertThatThrownBy(() -> stockRegisterStopService.cu91registerStopStock(requestDto, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El wallet no cuenta con las acciones suficientes para realizar el stop.");

            // Verify - No debe llamar a servicios cuando hay excepción
            verifyNoInteractions(orderRepository);
        }
    }
}

