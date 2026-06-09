package trinity.play2learn.backend.activity.activity.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public interface IActivityPaginatedRepository extends CrudRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    @Query("SELECT DISTINCT a FROM Activity a JOIN FETCH a.subject WHERE a.id IN :ids")
    List<Activity> findAllByIdInWithSubject(@Param("ids") Collection<Long> ids);
}
