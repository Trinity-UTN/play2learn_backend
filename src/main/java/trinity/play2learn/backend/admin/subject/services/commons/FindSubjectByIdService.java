package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.IFindSubjectByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class FindSubjectByIdService implements IFindSubjectByIdService {
    private final ISubjectRepository subjectRepository;

    @Override
    public Subject findByIdOrThrowException(Long id) {
        return subjectRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(( ) -> new NotFoundException("Subject with id " + id + " does not exist"));
    }
}
