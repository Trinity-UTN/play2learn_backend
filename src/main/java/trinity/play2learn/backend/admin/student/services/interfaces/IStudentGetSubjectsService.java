package trinity.play2learn.backend.admin.student.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.subject.dtos.SubjectSimplifiedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IStudentGetSubjectsService {
    
    List<SubjectSimplifiedResponseDto> cu124GetSubjectsByStudent(User user);
    
}
