package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface ISubjectGetByIdService {
    
    Subject findById(Long id);

    Subject findDeletedById(Long id);
}
