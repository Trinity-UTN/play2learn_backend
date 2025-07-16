package trinity.play2learn.backend.admin.subject.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface ISubjectPaginatedRepository extends CrudRepository<Subject, Long>, JpaSpecificationExecutor<Subject>{
    
}
