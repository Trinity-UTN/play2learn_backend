package trinity.play2learn.backend.benefits.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;

public interface IBenefitPurchasePaginatedRepository extends CrudRepository<BenefitPurchase, Long>, JpaSpecificationExecutor<BenefitPurchase>{
    
}
