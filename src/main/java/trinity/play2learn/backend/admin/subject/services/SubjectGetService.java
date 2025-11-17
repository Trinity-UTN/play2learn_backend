package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetService;

@Service
@AllArgsConstructor
public class SubjectGetService implements ISubjectGetService{
    
    private final ISubjectGetByIdService findSubjectByIdService;

    @Override
    public SubjectResponseDto cu33GetSubjectById(Long id) {
        
        return SubjectMapper.toSubjectDto(findSubjectByIdService.findById(id));
    }

}
