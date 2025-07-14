package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectUpdateRequestDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.IFindSubjectByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectUpdateService;
import trinity.play2learn.backend.admin.subject.services.interfaces.IValidateSubjectService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.IGetTeacherByIdService;

@Service
@AllArgsConstructor
public class SubjectUpdateService implements ISubjectUpdateService {

    private final ICourseGetByIdService courseGetByIdService;
    private final IGetTeacherByIdService getTeacherByIdService;
    private final IFindSubjectByIdService findSubjectByIdService;
    private final ISubjectRepository subjectRepository;
    private final IValidateSubjectService validateSubjectService;

    @Override
    public SubjectResponseDto cu29UpdateSubject(SubjectUpdateRequestDto subjectDto) {
        
        Subject subjectInDb = findSubjectByIdService.findByIdOrThrowException(subjectDto.getId()); //Lanza un NotFoundException si no se encuentra una materia con el ID proporcionado

        Course course = courseGetByIdService.get(subjectDto.getCourseId()); //De no encontrar un curso con el ID proporcionado, lanza una excepción NotFoundException
        
        validateSubjectService.subjectExistByNameAndCourse(subjectDto.getName(), course); //Lanza un conflict exception si la materia ya existe en el curso

        Teacher teacher = null;
        if (subjectDto.getTeacherId() != null) {
            teacher = getTeacherByIdService.getTeacherById(subjectDto.getTeacherId()); //De no encontrar un profesor con el ID proporcionado, lanza una excepción NotFoundException
        }
        //Si no se proporciona un ID de profesor, se establece en null

        //No es posible actualizar la lista de estudiantes de una materia mediante el endpoint update.
        //Aunque la materia cambie de opcional a obligatoria, se mantiene la lista de estudiantes de la materia original.
         
        Subject subjectToUpdate = SubjectMapper.toUpdateModel(subjectDto, course, teacher, subjectInDb.getStudents());

        return SubjectMapper.toSubjectDto(subjectRepository.save(subjectToUpdate));
    }
    
}
