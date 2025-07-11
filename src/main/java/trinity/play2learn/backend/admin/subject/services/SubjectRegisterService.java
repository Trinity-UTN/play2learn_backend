package trinity.play2learn.backend.admin.subject.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IGetStudentsByCourse;
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
    private final IGetStudentsByCourse courseGetStudentsService;
    private final ISubjectRepository subjectRepository;

    @Override
    public SubjectResponseDto cu28RegisterSubject(SubjectRequestDto subjectDto) {

        Course course = courseGetByIdService.get(subjectDto.getCourseId()); //De no encontrar un curso con el ID proporcionado, lanza una excepción NotFoundException
        
        Teacher teacher = null;
        if (subjectDto.getTeacherId() != null) {
            teacher = getTeacherByIdService.getTeacherById(subjectDto.getTeacherId()); //De no encontrar un profesor con el ID proporcionado, lanza una excepción NotFoundException
        }
        //Si no se proporciona un ID de profesor, se establece en null

        List<Student> students = new ArrayList<>(); //Lista vacia
        if (!subjectDto.getOptional()) {
            students = courseGetStudentsService.getStudentsByCourseId(subjectDto.getCourseId()); //Si la materia no es opcional, asigno a todos los estudiantes del curso a la misma.
        }
        //Si es opcional, no se asignan estudiantes.

        Subject subjectToSave = SubjectMapper.toSubject(subjectDto, course, teacher, students);

        return SubjectMapper.toSubjectDto(subjectRepository.save(subjectToSave));
    }

    
}
