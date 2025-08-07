package trinity.play2learn.backend.benefits.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitRepository extends CrudRepository<Benefit, Long>{
    
}
