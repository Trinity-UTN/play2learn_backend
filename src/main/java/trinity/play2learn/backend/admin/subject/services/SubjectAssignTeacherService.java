package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectAssignTeacherRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAssignTeacherService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;

@Service
@AllArgsConstructor
public class SubjectAssignTeacherService implements ISubjectAssignTeacherService {
    
    private final ISubjectRepository subjectRepository;
    private final ISubjectGetByIdService getSubjectByIdService;
    private final ITeacherGetByIdService getTeacherByIdService;

    @Override
    @Transactional
    public SubjectResponseDto cu49AssignTeacher(SubjectAssignTeacherRequestDto requestDto) {
        
        Subject subject = getSubjectByIdService.findById(requestDto.getSubjectId());

        Teacher teacher = getTeacherByIdService.findById(requestDto.getTeacherId());

        subject.setTeacher(teacher);
        
        return SubjectMapper.toSubjectDto(subjectRepository.save(subject));
    }
    
}
