package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.subject.dtos.SubjectAddResponseDto;

public interface ISubjectAddStudentsService {
    
    SubjectAddResponseDto cu36AddStudentsToSubject(Long subjectId, List<Long> studentIds); ;
}
