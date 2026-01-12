package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.dtos.StudentSimplificatedResponse;

public interface ISubjectListAllStudentsService {

    List<StudentSimplificatedResponse> cu101ListAllStudentsBySubject (Long subjectId);
    
}
