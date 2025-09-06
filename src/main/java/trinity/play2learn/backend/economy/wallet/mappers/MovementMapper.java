package trinity.play2learn.backend.economy.wallet.mappers;

import java.util.List;

import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.wallet.dtos.response.MovementResponseDto;

public class MovementMapper {

    public static final MovementResponseDto toDto (Transaction transaction) {
        return MovementResponseDto.builder()
            .amount(Math.round(transaction.getAmount() * 1000.0) / 1000.0)
            .createdAt(transaction.getCreatedAt())
            .description(transaction.getDescription())
            .type((transaction.getOrigin() == TransactionActor.ESTUDIANTE) ? "EGRESO" : "INGRESO")
            .build();
    }

    public static final List<MovementResponseDto> toDtoList (List<Transaction> transactions) {
        return transactions.stream().map(MovementMapper::toDto).toList();
    }
    
}
