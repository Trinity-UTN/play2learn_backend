package trinity.play2learn.backend.investment.stock.mappers;

import java.math.BigInteger;
import java.time.LocalDateTime;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderStop;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;

public class OrderMapper {

    public static Order toEntity (
        OrderType orderType,
        OrderState orderState,
        Stock stock,
        Wallet wallet,
        BigInteger quantity
    ){
        return Order.builder()
            .orderType(orderType)
            .orderState(orderState)
            .stock(stock)
            .wallet(wallet)
            .quantity(quantity)
            .pricePerUnit(stock.getCurrentPrice())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Order toStopEntity (
        OrderType orderType,
        OrderState orderState,
        Stock stock,
        Wallet wallet,
        BigInteger quantity,
        Double stopPrice,
        OrderStop orderStop
    ){
        return Order.builder()
            .orderType(orderType)
            .orderState(orderState)
            .stock(stock)
            .wallet(wallet)
            .quantity(quantity)
            .pricePerUnit(stopPrice)
            .createdAt(LocalDateTime.now())
            .orderStop(orderStop)
            .build();
    }

    public static StockBuyResponseDto toBuyDto (Order order) {
        return StockBuyResponseDto.builder()
            .id(order.getId())
            .pricePerUnit(order.getPricePerUnit())
            .quantity(order.getQuantity().intValue())
            .total(order.getPricePerUnit() * order.getQuantity().doubleValue())
            .createdAt(order.getCreatedAt().toString())
            .build();
    }

    public static StockSellResponseDto toSellDto (Order order) {
        return StockSellResponseDto.builder()
            .pricePerUnit(order.getPricePerUnit())
            .quantity(order.getQuantity())
            .total(order.getPricePerUnit() * order.getQuantity().doubleValue())
            .build();
    }

    
}
