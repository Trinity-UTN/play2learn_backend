package trinity.play2learn.backend.investment.fixedTermDeposit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;

public interface IFixedTermDepositRepository extends CrudRepository<FixedTermDeposit, Long>, JpaSpecificationExecutor<FixedTermDeposit> {
    
    List<FixedTermDeposit> findByFixedTermState(FixedTermState fixedTermState);

}
