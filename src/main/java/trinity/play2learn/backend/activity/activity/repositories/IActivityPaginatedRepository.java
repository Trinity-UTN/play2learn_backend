package trinity.play2learn.backend.activity.activity.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public interface IActivityPaginatedRepository extends CrudRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {
    
}
