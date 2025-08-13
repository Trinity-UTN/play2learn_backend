package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface ISubjectListByTeacherService {
    
    List<SubjectResponseDto> cu57ListSubjectsByTeacher(User user);
}
