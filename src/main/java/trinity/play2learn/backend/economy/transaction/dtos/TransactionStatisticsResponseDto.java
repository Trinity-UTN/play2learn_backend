package trinity.play2learn.backend.economy.transaction.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionStatisticsResponseDto {

    private LocalDateTime date;

    private Double total;
    
    private Double totalCirculation;

    private Double totalReserve;

    private Double totalSubject;

    private Double totalStudent;

    private Double totalActivity;
}
