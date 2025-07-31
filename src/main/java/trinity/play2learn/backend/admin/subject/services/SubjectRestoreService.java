package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRestoreService;

@Service
@AllArgsConstructor
public class SubjectRestoreService implements ISubjectRestoreService{
    
    private final ISubjectRepository subjectRepository;
    private final ISubjectGetByIdService findSubjectByIdService;

    @Override
    public SubjectResponseDto cu34RestoreSubject(Long id) {
        Subject subject = findSubjectByIdService.findDeletedById(id);

        subject.restore();
        
        return SubjectMapper.toSubjectDto(subjectRepository.save(subject));
    }
}
