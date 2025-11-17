package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectUnassignTeacherService;

@Service
@AllArgsConstructor
public class SubjectUnassignTeacherService implements ISubjectUnassignTeacherService{
    
    private final ISubjectRepository subjectRepository;
    private final ISubjectGetByIdService getSubjectByIdService;

    @Override
    @Transactional
    public SubjectResponseDto cu50UnassignTeacher(Long subjectId) {
    
        Subject subject = getSubjectByIdService.findById(subjectId);
        
        subject.setTeacher(null);
        
        return SubjectMapper.toSubjectDto(subjectRepository.save(subject));
    }
}
