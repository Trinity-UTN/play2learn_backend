package trinity.play2learn.backend.profile.avatar.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.profile.avatar.models.Aspect;

public interface IAspectRepository extends CrudRepository<Aspect, Long>, JpaSpecificationExecutor<Aspect> {

    boolean existsByName(String name);
    
    List<Aspect> findAllByDeletedAtIsNullOrderByTypeAscNameAsc();
}
