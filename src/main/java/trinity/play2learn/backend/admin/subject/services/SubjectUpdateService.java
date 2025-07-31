package trinity.play2learn.backend.admin.subject.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectUpdateRequestDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectUpdateService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectExistsByNameAndCourseService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;

@Service
@AllArgsConstructor
public class SubjectUpdateService implements ISubjectUpdateService {

    private final ICourseGetByIdService courseGetByIdService;
    private final ITeacherGetByIdService getTeacherByIdService;
    private final ISubjectGetByIdService findSubjectByIdService;
    private final ISubjectRepository subjectRepository;
    private final ISubjectExistsByNameAndCourseService validateSubjectService;

    @Override
    public SubjectResponseDto cu29UpdateSubject(Long id , SubjectUpdateRequestDto subjectDto) {
        
        Subject subjectInDb = findSubjectByIdService.findById(id); //Lanza un NotFoundException si no se encuentra una materia con el ID proporcionado

        Course course = courseGetByIdService.findById(subjectDto.getCourseId()); //De no encontrar un curso con el ID proporcionado, lanza una excepción NotFoundException
        
        validateSubjectService.existByNameAndCourseAndIdNot(subjectDto.getName(), course, id); //Lanza un conflict exception si la materia ya existe en el curso

        Teacher teacher = null;
        if (subjectDto.getTeacherId() != null) {
            teacher = getTeacherByIdService.findById(subjectDto.getTeacherId()); //De no encontrar un profesor con el ID proporcionado, lanza una excepción NotFoundException
        }
        //Si no se proporciona un ID de profesor, se establece en null

        //No es posible actualizar la lista de estudiantes de una materia mediante el endpoint update.
        //Aunque la materia cambie de opcional a obligatoria, se mantiene la lista de estudiantes de la materia original.

        List<Student> studentsAssigned = new ArrayList<>(subjectInDb.getStudents()); 
        //Esto evita un error 500 si asigno directamente la lista de estudiantes a la materia a actualizar

        Subject subjectToUpdate = SubjectMapper.toUpdateModel(id, subjectDto, course, teacher, studentsAssigned);

        return SubjectMapper.toSubjectDto(subjectRepository.save(subjectToUpdate));
    }
    
}
