package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface IFindSubjectByIdService {
    
    Subject findByIdOrThrowException(Long id);

    Subject findDeletedById(Long id);
}
