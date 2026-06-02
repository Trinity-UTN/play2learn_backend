package trinity.play2learn.backend.admin.subject.services;

import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentSimplificatedResponse;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListAllStudentsService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.models.Subject;


@Service
@AllArgsConstructor
public class SubjectListAllStudentsService implements ISubjectListAllStudentsService {
    
    private final ISubjectGetByIdService findSubjectByIdService;

    private final IStudentRepository studentRepository;

    @Override
    public List<StudentSimplificatedResponse> cu101ListAllStudentsBySubject(Long subjectId) {

        Subject subject = findSubjectByIdService.findById(subjectId);

        List<Student> students = studentRepository.findByCourseId(subject.getCourse().getId());
    
        List<StudentSimplificatedResponse> studentSimplificatedResponses = new ArrayList<>();

        //Añade los estudiantes que ya estan asignados a la materia
        for (Student student : subject.getStudents()) {
            studentSimplificatedResponses.add(
                StudentMapper.toSimplificatedDto(
                    student, 
                    true
                )
            );
        }
        //Añade los estudiantes que no estan asignados a la materia
        for (Student student : students) {
            if (!subject.getStudents().contains(student)) {
                studentSimplificatedResponses.add(
                    StudentMapper.toSimplificatedDto(
                        student, 
                        false
                    )
                );
            }
        }
        //Ordena la lista de estudiantes por el valor de registered
        studentSimplificatedResponses.sort(Comparator.comparing(StudentSimplificatedResponse::isRegistered).reversed());
        return studentSimplificatedResponses;
    }
}
