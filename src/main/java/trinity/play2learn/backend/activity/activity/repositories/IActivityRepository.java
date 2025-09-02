package trinity.play2learn.backend.activity.activity.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.activity.models.Activity;

public interface IActivityRepository extends CrudRepository<Activity, Long> {
    
}
