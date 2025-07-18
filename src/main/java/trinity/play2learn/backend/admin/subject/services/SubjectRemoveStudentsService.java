package trinity.play2learn.backend.admin.subject.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsGetByListService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectAddResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.IFindSubjectByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRemoveStudentsService;

@Service
@AllArgsConstructor
public class SubjectRemoveStudentsService implements ISubjectRemoveStudentsService {
    
    private final ISubjectRepository subjectRepository;
    private final IFindSubjectByIdService findSubjectByIdService;
   private final IStudentsGetByListService studentListService;

    @Override
    public SubjectAddResponseDto cu37RemoveStudentsFromSubject(Long subjectId, List<Long> studentIds) {

        Subject subject = findSubjectByIdService.findByIdOrThrowException(subjectId); //Lanza un 404 si no encuentra la materia con el id proporcionado

        List<Student> studentsToRemove = studentListService.getStudentsByIdList(studentIds); //Lanza un 404 si no encuentra algun estudiante con el id proporcionado

        subject.removeStudents(studentsToRemove); //Remueve los estudiantes de la materia

        return SubjectMapper.toAddDto(subjectRepository.save(subject));
    }

}
