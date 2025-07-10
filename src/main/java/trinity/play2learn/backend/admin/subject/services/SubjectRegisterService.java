package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectRequestDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRegisterService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.IGetTeacherByIdService;

@Service
@AllArgsConstructor
public class SubjectRegisterService implements ISubjectRegisterService {

    private final ICourseGetByIdService courseGetByIdService;
    private final IGetTeacherByIdService getTeacherByIdService;
    private final ISubjectRepository subjectRepository;

    @Override
    public SubjectResponseDto cu28RegisterSubject(SubjectRequestDto subjectDto) {

        Course course = courseGetByIdService.get(subjectDto.getCourseId()); //De no encontrar un curso con el ID proporcionado, lanza una excepción NotFoundException
        
        Teacher teacher;
        if (subjectDto.getTeacherId() != null) {
            teacher = getTeacherByIdService.getTeacherById(subjectDto.getTeacherId()); //De no encontrar un profesor con el ID proporcionado, lanza una excepción NotFoundException
        } else {
            teacher = null; //Si no se proporciona un ID de profesor, se establece en null
            
        }
        Subject subjectToSave = SubjectMapper.toSubject(subjectDto, course, teacher);

        return SubjectMapper.toSubjectDto(subjectRepository.save(subjectToSave));
    }

    
}
