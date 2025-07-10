package trinity.play2learn.backend.admin.subject.repositories;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface ISubjectRepository extends CrudRepository<Subject , Long> {
    
}
