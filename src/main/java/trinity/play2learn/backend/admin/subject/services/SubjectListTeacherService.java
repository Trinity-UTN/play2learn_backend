package trinity.play2learn.backend.admin.subject.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListByTeacherService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class SubjectListTeacherService implements ISubjectListByTeacherService {
    
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final ISubjectRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDto> cu57ListSubjectsByTeacher(User user) {
        
        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        List<Subject> subjects = subjectRepository.findAllByTeacherAndDeletedAtIsNull(teacher);

        return SubjectMapper.toDtoList(subjects);
    }
    
}
