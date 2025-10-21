package trinity.play2learn.backend.investment.stock.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;

public interface IStockHistoryRepository extends CrudRepository<StockHistory, Long>, JpaSpecificationExecutor<StockHistory> {
    
    Optional<StockHistory> findTopByStockOrderByCreatedAtDesc(Stock stock);

    List<StockHistory> findTop100ByStockOrderByCreatedAtAsc(Stock stock);

    List<StockHistory> findTop10ByStockOrderByCreatedAtAsc(Stock stock);

    List<StockHistory> findByStockAndCreatedAtBetweenOrderByCreatedAtAsc(
        Stock stock,
        LocalDateTime start,
        LocalDateTime end
    );

}
