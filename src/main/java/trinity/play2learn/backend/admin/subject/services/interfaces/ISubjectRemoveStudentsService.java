package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.subject.dtos.SubjectAddResponseDto;

public interface ISubjectRemoveStudentsService {
    
    SubjectAddResponseDto cu37RemoveStudentsFromSubject(Long subjectId, List<Long> studentIds);
}
