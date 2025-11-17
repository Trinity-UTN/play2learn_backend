package trinity.play2learn.backend.benefits.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitPaginatedRepository extends CrudRepository<Benefit, Long>, JpaSpecificationExecutor<Benefit>{
    
    
}
