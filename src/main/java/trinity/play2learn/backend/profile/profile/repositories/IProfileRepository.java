package trinity.play2learn.backend.profile.profile.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.profile.profile.models.Profile;

public interface IProfileRepository extends CrudRepository<Profile, Long> {

    
} 