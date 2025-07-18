package trinity.play2learn.backend.admin.student.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsGetByListService;

@Service
@AllArgsConstructor
public class StudentsGetByListService implements IStudentsGetByListService{
    
    private final IStudentGetByIdService studentGetByIdService;

    @Override
    public List<Student> getStudentsByIdList(List<Long> studentIds) {
        return studentIds
            .stream()
            .map(studentGetByIdService::get)
            .toList();
    }
}
