package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class SubjectGetByIdService implements ISubjectGetByIdService {
    private final ISubjectRepository subjectRepository;

    @Override
    public Subject findById(Long id) {
        return subjectRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(( ) -> new NotFoundException("Subject with id " + id + " does not exist"));
    }

    @Override
    public Subject findDeletedById(Long id) {
        return subjectRepository.findByIdAndDeletedAtIsNotNull(id).orElseThrow(
            () -> new NotFoundException("Subject with id " + id + " does not exist or is not eliminated")
        );
    }

    
}
