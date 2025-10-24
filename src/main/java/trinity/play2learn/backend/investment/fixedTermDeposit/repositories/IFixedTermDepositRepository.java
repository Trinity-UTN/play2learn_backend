package trinity.play2learn.backend.investment.fixedTermDeposit.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;

public interface IFixedTermDepositRepository extends CrudRepository<FixedTermDeposit, Long>, JpaSpecificationExecutor<FixedTermDeposit> {
    
}
