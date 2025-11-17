package trinity.play2learn.backend.admin.student.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsGetByIdListService;

@Service
@AllArgsConstructor
public class StudentsGetByIdListService implements IStudentsGetByIdListService{
    
    private final IStudentGetByIdService studentGetByIdService;

    @Override
    public List<Student> getStudentsByIdList(List<Long> studentIds) {
        return studentIds
            .stream()
            .map(studentGetByIdService::findById)
            .toList();
    }
}
