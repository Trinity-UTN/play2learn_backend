package trinity.play2learn.backend.economy.transaction.mappers;

import java.util.List;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionResponseDto;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.models.Order;

public class TransactionMapper {
    

    public static Transaction toModel (
        Double amount,
        String description,
        TransactionActor origin,
        TransactionActor destination,
        Wallet wallet,
        Subject subject,
        Activity activity,
        Benefit benefit,
        Order order,
        Reserve reserve
    ){
        return Transaction.builder()
        .amount(amount)
        .description(description)
        .origin(origin)
        .destination(destination)
        .wallet(wallet)
        .subject(subject)
        .activity(activity)
        .reserve(reserve)
        .benefit(benefit)
        .order(order)
        .build();
    }

    public static final TransactionResponseDto toDto (Transaction transaction) {
        return TransactionResponseDto.builder()
            .amount(Math.round(transaction.getAmount() * 1000.0) / 1000.0)
            .createdAt(transaction.getCreatedAt())
            .description(transaction.getDescription())
            .type((transaction.getOrigin() == TransactionActor.ESTUDIANTE) ? "EGRESO" : "INGRESO")
            .build();
    }

    public static final List<TransactionResponseDto> toDtoList (List<Transaction> transactions) {
        return transactions.stream().map(TransactionMapper::toDto).toList();
    }
 
}
