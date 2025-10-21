package trinity.play2learn.backend.investment.stock.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IStockRepository extends CrudRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    
    @SuppressWarnings("null")
    Optional<Stock> findById(Long id);


    List<Stock> findAll();
    
}
