package trinity.play2learn.backend.investment.stock.services.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.InvestmentTestMother;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderFindPendingServiceTest {

    @Mock
    private IOrderRepository orderRepository;

    private OrderFindPendingService orderFindPendingService;

    @BeforeEach
    void setUp() {
        orderFindPendingService = new OrderFindPendingService(orderRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given wallet and stock with pending orders When finding pending orders Then returns list of sell response DTOs")
        void whenPendingOrders_returnsListOfSellResponseDtos() {
            // Given - Wallet y acción con órdenes pendientes
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Stock stock = InvestmentTestMother.defaultStock();
            Order order1 = InvestmentTestMother.orderPending(InvestmentTestMother.DEFAULT_ORDER_ID,
                    trinity.play2learn.backend.investment.stock.models.OrderStop.LOSS,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE);
            Order order2 = InvestmentTestMother.orderPending(1102L,
                    trinity.play2learn.backend.investment.stock.models.OrderStop.PROFIT,
                    InvestmentTestMother.DEFAULT_CURRENT_PRICE + 10.0);
            List<Order> orders = Arrays.asList(order1, order2);

            StockSellResponseDto dto1 = InvestmentTestMother.stockSellResponseDto(order1.getQuantity(),
                    order1.getPricePerUnit(), order1.getPricePerUnit() * order1.getQuantity().doubleValue());
            StockSellResponseDto dto2 = InvestmentTestMother.stockSellResponseDto(order2.getQuantity(),
                    order2.getPricePerUnit(), order2.getPricePerUnit() * order2.getQuantity().doubleValue());
            List<StockSellResponseDto> expectedDtos = Arrays.asList(dto1, dto2);

            when(orderRepository.findByWalletAndStockAndOrderState(wallet, stock, OrderState.PENDIENTE))
                    .thenReturn(orders);

            // When - Buscar órdenes pendientes
            try (MockedStatic<OrderMapper> orderMapperMock = mockStatic(OrderMapper.class)) {
                orderMapperMock.when(() -> OrderMapper.toSellDtoList(orders)).thenReturn(expectedDtos);

                List<StockSellResponseDto> result = orderFindPendingService.execute(stock, wallet);

                // Then - Debe retornar lista de DTOs
                verify(orderRepository).findByWalletAndStockAndOrderState(wallet, stock, OrderState.PENDIENTE);
                orderMapperMock.verify(() -> OrderMapper.toSellDtoList(orders));
                assertThat(result).hasSize(2);
                assertThat(result).containsExactlyElementsOf(expectedDtos);
            }
        }

        @Test
        @DisplayName("Given wallet and stock with no pending orders When finding pending orders Then returns empty list")
        void whenNoPendingOrders_returnsEmptyList() {
            // Given - Sin órdenes pendientes
            Wallet wallet = InvestmentTestMother.defaultWallet();
            Stock stock = InvestmentTestMother.defaultStock();
            List<Order> orders = Collections.emptyList();

            when(orderRepository.findByWalletAndStockAndOrderState(wallet, stock, OrderState.PENDIENTE))
                    .thenReturn(orders);

            // When - Buscar órdenes pendientes
            try (MockedStatic<OrderMapper> orderMapperMock = mockStatic(OrderMapper.class)) {
                orderMapperMock.when(() -> OrderMapper.toSellDtoList(orders)).thenReturn(Collections.emptyList());

                List<StockSellResponseDto> result = orderFindPendingService.execute(stock, wallet);

                // Then - Debe retornar lista vacía
                assertThat(result).isEmpty();
            }
        }
    }
}

