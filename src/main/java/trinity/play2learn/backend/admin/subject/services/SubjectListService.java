package trinity.play2learn.backend.admin.subject.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListService;

@Service
@AllArgsConstructor
public class SubjectListService implements ISubjectListService{
    
    private final ISubjectRepository subjectRepository;
    
    @Override
    public List<SubjectResponseDto> cu31ListSubjects() {
        return SubjectMapper.toDtoList(subjectRepository.findAllByDeletedAtIsNull());
    }
}
