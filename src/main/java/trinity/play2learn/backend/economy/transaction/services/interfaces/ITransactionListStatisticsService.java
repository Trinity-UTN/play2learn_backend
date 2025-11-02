package trinity.play2learn.backend.economy.transaction.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.economy.transaction.dtos.TransactionStatisticsResponseDto;

public interface ITransactionListStatisticsService {

    public List<TransactionStatisticsResponseDto> execute();
    
}
