package trinity.play2learn.backend.activity.ordenarSecuencia.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.ordenarSecuencia.models.Event;

public interface IEventRepository extends CrudRepository<Event, Long> {

    
} 