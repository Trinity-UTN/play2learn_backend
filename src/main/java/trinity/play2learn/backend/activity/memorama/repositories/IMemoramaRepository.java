package trinity.play2learn.backend.activity.memorama.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.memorama.models.Memorama;

public interface IMemoramaRepository extends CrudRepository<Memorama, Long> {
    
}
