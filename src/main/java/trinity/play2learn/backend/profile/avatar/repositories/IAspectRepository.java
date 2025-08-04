package trinity.play2learn.backend.profile.avatar.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.profile.avatar.models.Aspect;

public interface IAspectRepository extends CrudRepository<Aspect, Long> {

    boolean existsByName(String name);
    
}
