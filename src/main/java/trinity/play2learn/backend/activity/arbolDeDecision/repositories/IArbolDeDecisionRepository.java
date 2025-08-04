package trinity.play2learn.backend.activity.arbolDeDecision.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.arbolDeDecision.models.ArbolDeDecisionActivity;

public interface IArbolDeDecisionRepository extends CrudRepository< ArbolDeDecisionActivity ,Long> {
    
}
