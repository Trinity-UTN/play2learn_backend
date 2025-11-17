package trinity.play2learn.backend.admin.subject.services;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsGetByIdListService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectAddResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddStudentsService;

@Service
@AllArgsConstructor
public class SubjectAddStudentsService implements ISubjectAddStudentsService {
    
    private final ISubjectRepository subjectRepository;
    private final ISubjectGetByIdService subjectGetService;

    private final IStudentsGetByIdListService studentsGetByListService;
    @Override
    public SubjectAddResponseDto cu36AddStudentsToSubject(Long subjectId, List<Long> studentIds) {

        Subject subject = subjectGetService.findById(subjectId); //Lanza un 404 si la materia no existe (o esta eliminada) 

        List<Student> studentsToAdd = studentsGetByListService.getStudentsByIdList(studentIds); //Lanza un 404 si alguno de los estudiantes de la lista no existe (O esta eliminado)
        
        //Este for es para evitar añadir estudiantes que ya estan asignados a la materia
        for (Student student : studentsToAdd) {
            if (!subject.getStudents().contains(student)) {
                subject.getStudents().add(student);
            } 
        }

        return SubjectMapper.toAddDto(subjectRepository.save(subject));

    }

    
}
